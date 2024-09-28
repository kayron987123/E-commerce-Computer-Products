package org.gad.ecommerce_computer_components.configuration.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.context.SecurityContextPersistenceFilter;

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
                        .requestMatchers(HttpMethod.POST, "/users/login/user").permitAll()
                        .requestMatchers(HttpMethod.POST, "/users/register/user").permitAll()
                        .requestMatchers(HttpMethod.POST, "/users/verifyToken/user").permitAll()
                        .requestMatchers(HttpMethod.POST, "/users/recoverPassword/user").permitAll()
                        .anyRequest().authenticated()
                ))
                .addFilterAfter(jwtAuthorizationFilter, SecurityContextPersistenceFilter.class);

        return http.build();
    }
}
