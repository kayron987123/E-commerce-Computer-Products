package org.gad.ecommerce_computer_components.service.impl;

import com.paypal.api.payments.Links;
import com.paypal.base.rest.PayPalRESTException;
import lombok.extern.slf4j.Slf4j;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.util.JRLoader;
import org.gad.ecommerce_computer_components.persistence.entity.*;
import org.gad.ecommerce_computer_components.persistence.enums.OrderStatus;
import org.gad.ecommerce_computer_components.persistence.enums.PaymentMethod;
import org.gad.ecommerce_computer_components.persistence.repository.*;
import org.gad.ecommerce_computer_components.presentation.dto.DtoReturn.ListShoppingCartDTO;
import org.gad.ecommerce_computer_components.presentation.dto.DtoReturn.ProductShoppingCartDTO;
import org.gad.ecommerce_computer_components.presentation.dto.request.OrderDTO;
import org.gad.ecommerce_computer_components.service.interfaces.OrderService;
import org.gad.ecommerce_computer_components.service.interfaces.PaypalService;
import org.gad.ecommerce_computer_components.service.interfaces.ProductService;
import org.gad.ecommerce_computer_components.service.interfaces.ShoppingCartService;
import org.gad.ecommerce_computer_components.utils.mappers.OrderMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

import java.io.*;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private ShoppingCartService shoppingCartService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderDetailRepository orderDetailRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private ProductService productService;
    @Autowired
    private ShoppingCartRepository shoppingCartRepository;
    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private PaypalService paypalService;

    @Override
    public String createOrder(OrderDTO orderDTO, Long userId) throws PayPalRESTException {

        List<ListShoppingCartDTO> listShoppingCartDTOS = shoppingCartService.getCart(userId);
        if (listShoppingCartDTOS.isEmpty()){
            throw new IllegalArgumentException("No shopping cart found");
        }

        // Crea la orden solo una vez
        Order order = OrderMapper.INSTANCE.OrderDTOToOrder(orderDTO);
        order.setUser(userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("User not found")));

        // Calcula el total a pagar por todos los productos en el carrito
        BigDecimal totalToPay = listShoppingCartDTOS.stream()
                .map(item -> item.getProduct().getPrice().multiply(BigDecimal.valueOf(item.getAmount())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Establece los detalles de la orden
        order.setTotalToPay(totalToPay);
        order.setDeliveryDate(LocalDateTime.now().plusDays(1).plusHours(2).plusMinutes(30));
        order.setStatus(OrderStatus.PENDIENTE);

        com.paypal.api.payments.Payment payment = paypalService.createPayment(
                (totalToPay.doubleValue() / 3.74), "USD", "paypal", "sale", "Payment for Order",
                "http://localhost:8080/paypal/cancel", "http://localhost:8080/paypal/success");

        // Guarda la orden en la base de datos
        orderRepository.save(order);

        // Recorre los productos del carrito y crea un detalle de la orden para cada producto
        for (ListShoppingCartDTO item : listShoppingCartDTOS) {
            ProductShoppingCartDTO product = item.getProduct();
            Integer amount = item.getAmount();

            productService.updateProductStock(product.getId(), amount);

            OrderDetail orderDetail = OrderDetail.builder()
                    .order(order)
                    .product(Product.builder().id(product.getId()).build())
                    .amount(amount)
                    .unitPrice(product.getPrice())
                    .build();

            // Guarda cada detalle de la orden
            orderDetailRepository.save(orderDetail);

            ShoppingCart shoppingCart = new ShoppingCart();
            shoppingCart.setUser(userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("User not found")));
            shoppingCart.setProduct(productRepository.findById(product.getId()).orElseThrow(() -> new IllegalArgumentException("Product not found")));
            shoppingCart.setAmount(amount);
            shoppingCartRepository.save(shoppingCart);
        }

        // Crea un pago para la orden
        Payment paymentEntity = Payment.builder()
                .order(order)
                .paymentMethod(PaymentMethod.PAYPAL)  // Cambia el método de pago según sea necesario
                .amount(totalToPay)
                .build();

        // Guarda el pago en la base de datos
        paymentRepository.save(paymentEntity);

        // Limpia el carrito de compras después de realizar la orden
        shoppingCartService.clearCart(userId);

        return payment.getLinks().stream()
                .filter(link -> link.getRel().equals("approval_url"))
                .findFirst()
                .map(Links::getHref)
                .orElseThrow(() -> new IllegalArgumentException("No PayPal approval link found"));
    }

}
