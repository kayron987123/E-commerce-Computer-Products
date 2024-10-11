package org.gad.ecommerce_computer_components.service.interfaces;

import com.paypal.base.rest.PayPalRESTException;
import net.sf.jasperreports.engine.JRException;
import org.gad.ecommerce_computer_components.presentation.dto.request.OrderDTO;
import org.springframework.core.io.Resource;
import org.springframework.data.jpa.repository.Query;
import org.springframework.http.ResponseEntity;

import java.io.FileNotFoundException;

public interface OrderService {
    String createOrder(OrderDTO orderDTO, Long userId) throws PayPalRESTException;
}
