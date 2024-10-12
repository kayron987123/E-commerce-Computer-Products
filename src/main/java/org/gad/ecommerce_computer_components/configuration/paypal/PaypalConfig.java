package org.gad.ecommerce_computer_components.configuration.paypal;

import com.paypal.base.rest.APIContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PaypalConfig {

    @Value("${PAYPAL_CLIENT_ID}")
    private String clientId;

    @Value("${PAYPAL_CLIENT_SECRET}")
    private String clienteSecret;

    @Value("${PAYPAL_MODE}")
    private String mode;

    @Bean
    public APIContext apiContext() {
        return new APIContext(clientId, clienteSecret, mode);
    }
}
