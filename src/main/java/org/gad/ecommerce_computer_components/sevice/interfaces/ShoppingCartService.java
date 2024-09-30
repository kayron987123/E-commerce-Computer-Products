package org.gad.ecommerce_computer_components.sevice.interfaces;

import org.gad.ecommerce_computer_components.presentation.dto.ListShoppingCartDTO;
import org.gad.ecommerce_computer_components.presentation.dto.ShoppingCartDTO;

import java.util.List;

public interface ShoppingCartService {
    void addProductToCart(Long userId, ShoppingCartDTO shoppingCartDTO);
    List<ListShoppingCartDTO> getCart(Long userId);
    void removeProductFromCart(Long userId, ShoppingCartDTO shoppingCartDTO);
    void clearCart(Long userId);

    Long extractUserIdFromToken(String authorizationHeader);

}
