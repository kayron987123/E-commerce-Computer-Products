package org.gad.ecommerce_computer_components.presentation.controller;

import com.paypal.base.rest.PayPalRESTException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletResponse;
import net.sf.jasperreports.engine.JRException;
import org.gad.ecommerce_computer_components.persistence.entity.Order;
import org.gad.ecommerce_computer_components.persistence.repository.OrderRepository;
import org.gad.ecommerce_computer_components.presentation.dto.request.OrderDTO;
import org.gad.ecommerce_computer_components.presentation.dto.response.ApiResponse;
import org.gad.ecommerce_computer_components.service.interfaces.OrderService;
import org.gad.ecommerce_computer_components.service.interfaces.ShoppingCartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.FileNotFoundException;
import java.io.IOException;

@RestController
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private ShoppingCartService shoppingCartService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderRepository orderRepository;

    @PostMapping("/createOrder")
    public ResponseEntity<ApiResponse> createOrder(@RequestHeader(value = "Authorization") String authorizationHeader,
                                                   @RequestBody OrderDTO orderDTO){

        Long idUser = shoppingCartService.extractUserIdFromToken(authorizationHeader);
        if (idUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse(HttpStatus.UNAUTHORIZED.value(), "Invalid token"));
        }

        try {
            String approvalLink = orderService.createOrder(orderDTO, idUser);
            return ResponseEntity.ok(new ApiResponse(HttpStatus.OK.value(), approvalLink));
        } catch (PayPalRESTException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Payment error: " + e.getMessage()));
        }
    }
}
