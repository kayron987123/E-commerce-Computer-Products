package org.gad.ecommerce_computer_components.sevice.impl;

import org.gad.ecommerce_computer_components.persistence.entity.ShoppingCart;
import org.gad.ecommerce_computer_components.presentation.dto.ShoppingCartDTO;
import org.gad.ecommerce_computer_components.sevice.interfaces.ShoppingCartService;
import org.gad.ecommerce_computer_components.utils.mappers.ShoppingCartMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class ShoppingCartServiceImpl implements ShoppingCartService {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private static final String CART_KEY_PREFIX = "cart:";

    @Override
    public void addProductToCart(Long userId, ShoppingCartDTO shoppingCartDTO) {
        ShoppingCart shoppingCart = ShoppingCartMapper.INSTANCE.toEntity(shoppingCartDTO);
        String key = CART_KEY_PREFIX + userId;
        redisTemplate.opsForList().rightPush(key, shoppingCart);
        redisTemplate.expire(key, 30, TimeUnit.MINUTES);
    }

    @Override
    public List<ShoppingCartDTO> getCart(Long userId) {
        String key = CART_KEY_PREFIX + userId;
        List<Object> cartItems = redisTemplate.opsForList().range(key, 0, -1);
        return cartItems.stream()
                .map(item -> ShoppingCartMapper.INSTANCE.toDTO((ShoppingCart) item))
                .collect(Collectors.toList());
    }

    @Override
    public void removeProductFromCart(Long userId, ShoppingCartDTO shoppingCartDTO) {
        ShoppingCart shoppingCart = ShoppingCartMapper.INSTANCE.toEntity(shoppingCartDTO);
        String key = CART_KEY_PREFIX + userId;
        redisTemplate.opsForList().remove(key, 1, shoppingCart);
    }

    @Override
    public void clearCart(Long userId) {
        String key = CART_KEY_PREFIX + userId;
        redisTemplate.delete(key);
    }
}
