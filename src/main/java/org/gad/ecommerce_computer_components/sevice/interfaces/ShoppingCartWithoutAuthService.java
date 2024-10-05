package org.gad.ecommerce_computer_components.sevice.interfaces;

import org.gad.ecommerce_computer_components.presentation.dto.DtoReturn.ListShoppingCartDTO;
import org.gad.ecommerce_computer_components.presentation.dto.request.ShoppingCartDTO;

import java.util.List;

public interface ShoppingCartWithoutAuthService {
    String createTempCart();
    void addProductToTempCart(String cartId, ShoppingCartDTO shoppingCartDTO);
    List<ListShoppingCartDTO> getTempCartItems(String cartId);
    void clearTempCart(String cartId);
    void removeProductFromCart(String cartId, ShoppingCartDTO shoppingCartDTO);
}
