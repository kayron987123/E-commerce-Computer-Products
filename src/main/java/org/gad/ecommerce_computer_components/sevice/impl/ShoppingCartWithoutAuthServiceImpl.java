package org.gad.ecommerce_computer_components.sevice.impl;

import jakarta.transaction.Transactional;
import org.gad.ecommerce_computer_components.persistence.entity.Product;
import org.gad.ecommerce_computer_components.persistence.entity.ShoppingCart;
import org.gad.ecommerce_computer_components.persistence.entity.UserEntity;
import org.gad.ecommerce_computer_components.persistence.repository.ProductRepository;
import org.gad.ecommerce_computer_components.presentation.dto.ListShoppingCartDTO;
import org.gad.ecommerce_computer_components.presentation.dto.ShoppingCartDTO;
import org.gad.ecommerce_computer_components.sevice.interfaces.ProductService;
import org.gad.ecommerce_computer_components.sevice.interfaces.ShoppingCartWithoutAuthService;
import org.gad.ecommerce_computer_components.utils.mappers.ShoppingCartMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.data.redis.core.RedisKeyExpiredEvent;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class ShoppingCartWithoutAuthServiceImpl implements ShoppingCartWithoutAuthService {

    private static final long CART_EXPIRATION_MINUTES = 1000;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductService productService;

    @Override
    public String createTempCart() {
        String cartId = UUID.randomUUID().toString();
        String key = "tempCart:" + cartId;
        redisTemplate.expire(key, CART_EXPIRATION_MINUTES, TimeUnit.MINUTES);
        return cartId;
    }

    @Transactional
    @Override
    public void addProductToTempCart(String cartId, ShoppingCartDTO shoppingCartDTO) {
        String key = "tempCart:" + cartId;
        List<Object> cartItems = redisTemplate.opsForList().range(key, 0, -1);

        int quantityToAdd = (shoppingCartDTO.getAmount() != null && shoppingCartDTO.getAmount() > 0)
                ? shoppingCartDTO.getAmount() : 1;

        Optional<Product> productOpt = productRepository.findById(shoppingCartDTO.getProductId());
        if (!productOpt.isPresent()) {
            throw new IllegalArgumentException("Product not found");
        }
        Product product = productOpt.get();

        boolean productUpdated = false;
        for (int i = 0; i < cartItems.size(); i++) {
            ShoppingCart existingCartItem = (ShoppingCart) cartItems.get(i);
            if (existingCartItem.getProduct().getId().equals(shoppingCartDTO.getProductId())) {
                int newQuantity = existingCartItem.getAmount() + quantityToAdd;
                if (newQuantity <= product.getStock()) {
                    existingCartItem.setAmount(newQuantity);
                    productService.updateProductStock(shoppingCartDTO.getProductId(), quantityToAdd);
                    redisTemplate.opsForList().set(key, i, existingCartItem);
                    productUpdated = true;
                } else {
                    throw new IllegalArgumentException("Not enough stock available");
                }
                break;
            }
        }

        if (!productUpdated) {
            ShoppingCart newCartItem = new ShoppingCart();
            newCartItem.setProduct(product);
            newCartItem.setAmount(quantityToAdd);
            newCartItem.setUser(null);

            if (quantityToAdd <= product.getStock()) {
                productService.updateProductStock(shoppingCartDTO.getProductId(), quantityToAdd);
                redisTemplate.opsForList().rightPush(key, newCartItem);
            } else {
                throw new IllegalArgumentException("Not enough stock available");
            }
        }

        redisTemplate.expire(key, CART_EXPIRATION_MINUTES, TimeUnit.MINUTES);
    }

    @Override
    public List<ListShoppingCartDTO> getTempCartItems(String cartId) {
        String key = "tempCart:" + cartId;
        List<Object> cartItems = redisTemplate.opsForList().range(key, 0, -1);
        return cartItems.stream()
                .map(item -> ShoppingCartMapper.INSTANCE.toListDTO((ShoppingCart) item))
                .collect(Collectors.toList());
    }

    @Override
    public void clearTempCart(String cartId) {
        String key = "tempCart:" + cartId;
        List<Object> cartItems = redisTemplate.opsForList().range(key, 0, -1);

        for (Object item : cartItems) {
            ShoppingCart cartItem = (ShoppingCart) item;
            productService.updateProductReturnStockRedis(cartItem.getProduct().getId(), cartItem.getAmount());
        }

        redisTemplate.delete(key);
    }

    @Override
    public void removeProductFromCart(String cartId, ShoppingCartDTO shoppingCartDTO) throws IllegalArgumentException {
        String key = "tempCart:" + cartId;
        List<Object> cartItems = redisTemplate.opsForList().range(key, 0, -1);

        int quantityToRemove = (shoppingCartDTO.getAmount() != null && shoppingCartDTO.getAmount() > 0)
                ? shoppingCartDTO.getAmount() : 1;

        for (int i = 0; i < cartItems.size(); i++) {
            ShoppingCart existingCartItem = (ShoppingCart) cartItems.get(i);
            if (existingCartItem.getProduct().getId().equals(shoppingCartDTO.getProductId())) {
                int currentQuantity = existingCartItem.getAmount();

                // Verificar si la cantidad a remover es mayor que la cantidad actual en el carrito
                if (quantityToRemove > currentQuantity) {
                    throw new IllegalArgumentException("Cannot remove more items than currently in cart");
                }

                int newQuantity = currentQuantity - quantityToRemove;
                if (newQuantity > 0) {
                    // Actualizar la cantidad en el carrito
                    existingCartItem.setAmount(newQuantity);
                    productService.updateProductReturnStockRedis(shoppingCartDTO.getProductId(), quantityToRemove);
                    redisTemplate.opsForList().set(key, i, existingCartItem);
                } else {
                    // Eliminar el producto del carrito si la cantidad nueva es cero
                    redisTemplate.opsForList().remove(key, 1, existingCartItem);
                    productService.updateProductReturnStockRedis(shoppingCartDTO.getProductId(), currentQuantity);
                }
                return;
            }
        }

        throw new IllegalArgumentException("Product not found in cart");
    }
}
