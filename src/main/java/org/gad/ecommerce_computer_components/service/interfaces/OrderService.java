package org.gad.ecommerce_computer_components.service.interfaces;

import com.paypal.base.rest.PayPalRESTException;
import org.gad.ecommerce_computer_components.presentation.dto.request.OrderDTO;

public interface OrderService {
    String createOrder(OrderDTO orderDTO, Long userId) throws PayPalRESTException;
}
