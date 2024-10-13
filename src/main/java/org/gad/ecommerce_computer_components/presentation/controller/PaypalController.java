package org.gad.ecommerce_computer_components.presentation.controller;

import com.paypal.api.payments.Payment;
import com.paypal.base.rest.PayPalRESTException;
import org.gad.ecommerce_computer_components.service.interfaces.PaypalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/paypal")
public class PaypalController {

    @Autowired
    private PaypalService paypalService;

    @GetMapping("/cancel")
    public String cancelPay() {
        return "Payment cancelled";
    }

    @GetMapping("/success")
    public String successPay(@RequestParam("paymentId") String paymentId, @RequestParam("PayerID") String payerId) {
        try {
            Payment payment = paypalService.executePayment(paymentId, payerId);
            if (payment.getState().equals("approved")) {
                return "Payment success " + payment.getId();
            }
        } catch (PayPalRESTException e) {
            e.printStackTrace();
        }
        return "Payment failed";
    }
}
