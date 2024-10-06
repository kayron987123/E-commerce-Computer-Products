package org.gad.ecommerce_computer_components.service.interfaces;

public interface CartTransferService {
    void transferTempCartToUserCart(String tempCartId, Long userId);
}