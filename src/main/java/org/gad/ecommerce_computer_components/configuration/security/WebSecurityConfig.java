package org.gad.ecommerce_computer_components.configuration.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.context.SecurityContextPersistenceFilter;

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
                .authorizeHttpRequests((authz -> authz
                        //ForUSers
                        .requestMatchers(HttpMethod.POST, "/users/login/user").permitAll()
                        .requestMatchers(HttpMethod.POST, "/users/register/user").permitAll()
                        .requestMatchers(HttpMethod.POST, "/users/verifyToken/user").permitAll()
                        .requestMatchers(HttpMethod.POST, "/users/recoverPassword/user").permitAll()
                        .requestMatchers(HttpMethod.DELETE, "/users/delete/user").hasRole(USUARIO.name())
                        .requestMatchers(HttpMethod.PUT, "/users/update/").hasRole(USUARIO.name())
                        .requestMatchers(HttpMethod.PUT, "/users/updateStatus/user/{id}").hasRole(ADMINISTRADOR.name())

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
                        .anyRequest().authenticated()

                ))
                .addFilterAfter(jwtAuthorizationFilter, SecurityContextPersistenceFilter.class);

        return http.build();
    }
}
