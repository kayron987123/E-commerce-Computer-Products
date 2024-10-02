package org.gad.ecommerce_computer_components.sevice.impl;

import org.gad.ecommerce_computer_components.presentation.dto.ListShoppingCartDTO;
import org.gad.ecommerce_computer_components.presentation.dto.ShoppingCartDTO;
import org.gad.ecommerce_computer_components.sevice.interfaces.CartTransferService;
import org.gad.ecommerce_computer_components.sevice.interfaces.ShoppingCartService;
import org.gad.ecommerce_computer_components.sevice.interfaces.ShoppingCartWithoutAuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CartTransferServiceImpl implements CartTransferService {

    @Autowired
    private ShoppingCartWithoutAuthService shoppingCartWithoutAuthService;

    @Autowired
    private ShoppingCartService shoppingCartService;

    @Override
    public void transferTempCartToUserCart(String tempCartId, Long userId) {
        List<ListShoppingCartDTO> temCartItems = shoppingCartWithoutAuthService.getTempCartItems(tempCartId);

        for (ListShoppingCartDTO item : temCartItems) {
            ShoppingCartDTO cartDTO = new ShoppingCartDTO();
            cartDTO.setProductId(item.getProduct().getId());
            cartDTO.setAmount(item.getAmount());

            shoppingCartService.addProductToCart(userId, cartDTO);
        }
        shoppingCartWithoutAuthService.clearTempCart(tempCartId);
    }
}
