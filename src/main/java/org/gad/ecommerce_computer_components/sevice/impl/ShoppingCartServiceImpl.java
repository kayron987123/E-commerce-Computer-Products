package org.gad.ecommerce_computer_components.sevice.impl;

import io.jsonwebtoken.Claims;
import jakarta.transaction.Transactional;
import org.gad.ecommerce_computer_components.persistence.entity.Product;
import org.gad.ecommerce_computer_components.persistence.entity.ShoppingCart;
import org.gad.ecommerce_computer_components.persistence.entity.UserEntity;
import org.gad.ecommerce_computer_components.persistence.repository.ProductRepository;
import org.gad.ecommerce_computer_components.persistence.repository.UserRepository;
import org.gad.ecommerce_computer_components.presentation.dto.ListShoppingCartDTO;
import org.gad.ecommerce_computer_components.presentation.dto.ShoppingCartDTO;
import org.gad.ecommerce_computer_components.sevice.interfaces.ProductService;
import org.gad.ecommerce_computer_components.sevice.interfaces.ShoppingCartService;
import org.gad.ecommerce_computer_components.sevice.interfaces.UserService;
import org.gad.ecommerce_computer_components.utils.mappers.ShoppingCartMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class ShoppingCartServiceImpl implements ShoppingCartService {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private static final String CART_KEY_PREFIX = "cart:";
    private static final int EXPIRATION_MINUTES = 60;

    @Autowired
    private UserService userService;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private ProductService productService;
    @Autowired
    private UserRepository userRepository;

    @Transactional
    @Override
    public void addProductToCart(Long userId, ShoppingCartDTO shoppingCartDTO) {
        String key = CART_KEY_PREFIX + userId;
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
            Optional<UserEntity> userEntity = userRepository.findById(userId);
            if (!userEntity.isPresent()) {
                throw new IllegalArgumentException("User not found");
            }
            newCartItem.setUser(userEntity.get());

            if (quantityToAdd <= product.getStock()) {
                productService.updateProductStock(shoppingCartDTO.getProductId(), quantityToAdd);
                redisTemplate.opsForList().rightPush(key, newCartItem);
            } else {
                throw new IllegalArgumentException("Not enough stock available");
            }
        }

        redisTemplate.expire(key, EXPIRATION_MINUTES, TimeUnit.MINUTES);
    }

    @Override
    public List<ListShoppingCartDTO> getCart(Long userId) {
        String key = CART_KEY_PREFIX + userId;
        List<Object> cartItems = redisTemplate.opsForList().range(key, 0, -1);
        return cartItems.stream()
                .map(item -> ShoppingCartMapper.INSTANCE.toListDTO((ShoppingCart) item))
                .collect(Collectors.toList());
    }

    @Override
    public void removeProductFromCart(Long userId, ShoppingCartDTO shoppingCartDTO) {
        String key = CART_KEY_PREFIX + userId;
        List<Object> cartItems = redisTemplate.opsForList().range(key, 0, -1);

        int quantityToRemove = (shoppingCartDTO.getAmount() != null && shoppingCartDTO.getAmount() > 0)
                ? shoppingCartDTO.getAmount() : 1;

        for (int i = 0; i < cartItems.size(); i++) {
            ShoppingCart existingCartItem = (ShoppingCart) cartItems.get(i);
            if (existingCartItem.getProduct().getId().equals(shoppingCartDTO.getProductId())) {
                int newQuantity = existingCartItem.getAmount() - quantityToRemove;
                if (newQuantity > 0) {
                    existingCartItem.setAmount(newQuantity);
                    productService.updateProductStock(shoppingCartDTO.getProductId(), quantityToRemove);
                    redisTemplate.opsForList().set(key, i, existingCartItem);
                } else {
                    redisTemplate.opsForList().remove(key, 1, existingCartItem);
                    productService.updateProductStock(shoppingCartDTO.getProductId(), existingCartItem.getAmount());
                }
                return;
            }
        }

        throw new IllegalArgumentException("Product not found in cart");
    }

    @Override
    public void clearCart(Long userId) {
        String key = CART_KEY_PREFIX + userId;
        List<Object> cartItems = redisTemplate.opsForList().range(key, 0, -1);

        for (Object item : cartItems) {
            ShoppingCart cartItem = (ShoppingCart) item;
            productService.updateProductStock(cartItem.getProduct().getId(), cartItem.getAmount());
        }

        redisTemplate.delete(key);
    }

    @Override
    public Long extractUserIdFromToken(String authorizationHeader) {
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String tokenJWT = authorizationHeader.substring(7);
            Claims claims = userService.extractClaimsFromJWT(tokenJWT);
            return claims.get("id", Long.class);
        }
        throw new IllegalArgumentException("Invalid token");
    }
}
