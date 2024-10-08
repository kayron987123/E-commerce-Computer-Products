package org.gad.ecommerce_computer_components.configuration.paypal;

import com.paypal.base.rest.APIContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PaypalConfig {

    @Value("${PAYPAL.CLIENT-ID}")
    private String clientId;

    @Value("${PAYPAL.CLIENT-SECRET}")
    private String clienteSecret;

    @Value("${PAYPAL.MODE}")
    private String mode;

    @Bean
    public APIContext apiContext() {
        return new APIContext(clientId, clienteSecret, mode);
    }
}
