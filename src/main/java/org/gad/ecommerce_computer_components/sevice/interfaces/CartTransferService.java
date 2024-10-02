package org.gad.ecommerce_computer_components.sevice.interfaces;

public interface CartTransferService {
    void transferTempCartToUserCart(String tempCartId, Long userId);
}
