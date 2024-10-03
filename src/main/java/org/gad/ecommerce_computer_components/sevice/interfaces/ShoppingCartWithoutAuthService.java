package org.gad.ecommerce_computer_components.sevice.interfaces;

import org.gad.ecommerce_computer_components.presentation.dto.ListShoppingCartDTO;
import org.gad.ecommerce_computer_components.presentation.dto.ShoppingCartDTO;
import org.springframework.data.redis.core.RedisKeyExpiredEvent;

import java.util.List;

public interface ShoppingCartWithoutAuthService {
    String createTempCart();
    void addProductToTempCart(String cartId, ShoppingCartDTO shoppingCartDTO);
    List<ListShoppingCartDTO> getTempCartItems(String cartId);
    void clearTempCart(String cartId);
    void removeProductFromCart(String cartId, ShoppingCartDTO shoppingCartDTO);
}
