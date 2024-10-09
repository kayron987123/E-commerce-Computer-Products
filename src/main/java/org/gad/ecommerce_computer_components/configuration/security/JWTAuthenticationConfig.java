package org.gad.ecommerce_computer_components.configuration.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.gad.ecommerce_computer_components.persistence.entity.UserEntity;
import org.gad.ecommerce_computer_components.persistence.enums.AccountStatement;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static org.gad.ecommerce_computer_components.configuration.security.Constants.*;

@Configuration
public class JWTAuthenticationConfig {
    public String getJWTToken(UserEntity user){
        if (user.getAccountStatus().equals(AccountStatement.ELIMINADO)) {
            throw new IllegalStateException("You cannot generate a token for a deleted user.");
        }

        List<String> roles = Collections.singletonList(user.getRole().name());

        List<GrantedAuthority> grantedAuthorities = roles.stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                .collect(Collectors.toList());

        String token = Jwts
                .builder()
                .setId(user.getUsername())
                .setSubject(user.getUsername())
                .claim("authorities",
                        grantedAuthorities.stream()
                                .map(GrantedAuthority::getAuthority)
                                .collect(Collectors.toList()))
                .claim("id", user.getId())
                .claim("email", user.getEmail())
                .claim("name", user.getName())
                .claim("lastName", user.getLastName())
                .claim("role", user.getRole().name())
                .claim("accountStatus", user.getAccountStatus().name())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + TOKEN_EXPIRATION_TIME))
                .signWith(getSigningKey(SUPER_SECRET_KEY), SignatureAlgorithm.HS512).compact();
        return "Bearer " + token;
    }
}
