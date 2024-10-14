package org.gad.ecommerce_computer_components.configuration.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.context.SecurityContextPersistenceFilter;

import java.util.List;

import static org.gad.ecommerce_computer_components.persistence.enums.Role.*;

@EnableWebSecurity
@Configuration
public class WebSecurityConfig {
    @Autowired
    JWTAuthorizationFilter jwtAuthorizationFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf((csrf -> csrf.disable()))
                .cors(cors -> cors.configurationSource(request -> {
                    var corsConfig = new org.springframework.web.cors.CorsConfiguration();
                    corsConfig.setAllowedOrigins(List.of("http://localhost:3000"));
                    corsConfig.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
                    corsConfig.setAllowedHeaders(List.of("*"));
                    corsConfig.setExposedHeaders(List.of("Authorization"));
                    corsConfig.setAllowCredentials(true);
                    return corsConfig;
                }))
                .authorizeHttpRequests((authz -> authz
                        //ForUSers
                        .requestMatchers(HttpMethod.POST, "/users/login/user").permitAll()
                        .requestMatchers(HttpMethod.POST, "/users/register/user").permitAll()
                        .requestMatchers(HttpMethod.POST, "/users/verifyToken/user").permitAll()
                        .requestMatchers(HttpMethod.POST, "/users/recoverPassword/user").permitAll()
                        .requestMatchers(HttpMethod.DELETE, "/users/delete/user").hasRole(USUARIO.name())
                        .requestMatchers(HttpMethod.PUT, "/users/update/").hasRole(USUARIO.name())
                        .requestMatchers(HttpMethod.PUT, "/users/updateStatus/user/{id}").hasRole(ADMINISTRADOR.name())
                        .requestMatchers(HttpMethod.POST, "/users/logout/user").hasRole(USUARIO.name())

                        //ForProducts
                        .requestMatchers(HttpMethod.GET, "/product/list/").permitAll()

                        //ForShoppingCart
                        .requestMatchers(HttpMethod.POST, "/shopping-carts/addProduct/cart").hasRole(USUARIO.name())
                        .requestMatchers(HttpMethod.GET, "/shopping-carts/getListCarts/cart").hasRole(USUARIO.name())
                        .requestMatchers(HttpMethod.DELETE, "/shopping-carts/removeProduct/cart").hasRole(USUARIO.name())
                        .requestMatchers(HttpMethod.DELETE, "/shopping-carts/clearCart").hasRole(USUARIO.name())

                        //ForShoppingCartWithoutAuth
                        .requestMatchers(HttpMethod.POST, "/noauth/shopping-carts/createTempCart/cart").permitAll()
                        .requestMatchers(HttpMethod.POST, "/noauth/shopping-carts/{cartId}/addProduct/cart").permitAll()
                        .requestMatchers(HttpMethod.GET, "/noauth/shopping-carts/{cartId}/getTempCartItems/cart").permitAll()
                        .requestMatchers(HttpMethod.DELETE, "/noauth/shopping-carts/{cartId}/removeProduct/cart").permitAll()
                        .requestMatchers(HttpMethod.DELETE, "/noauth/shopping-carts/{cartId}/clearTempCart/cart").permitAll()

                        //ForOders
                        .requestMatchers(HttpMethod.POST, "/order/createOrder").hasRole(USUARIO.name())
                        .requestMatchers(HttpMethod.GET, "/order/createReport").hasRole(USUARIO.name())

                        //ForReviews
                        .requestMatchers(HttpMethod.POST, "/reviews/create/review/{productId}/product").hasRole(USUARIO.name())
                        .requestMatchers(HttpMethod.DELETE, "/reviews/delete/review/{reviewId}").hasRole(USUARIO.name())
                        .requestMatchers(HttpMethod.PUT, "/reviews/update/review/{reviewId}").hasRole(USUARIO.name())

                        //ForPaypal
                        .requestMatchers(HttpMethod.GET, "/paypal/cancel").permitAll()
                        .requestMatchers(HttpMethod.GET, "/paypal/success").permitAll()

                        .anyRequest().authenticated()

                ))
                .addFilterAfter(jwtAuthorizationFilter, SecurityContextPersistenceFilter.class);

        return http.build();
    }
}
