package org.gad.ecommerce_computer_components.configuration.security;

import io.jsonwebtoken.*;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.gad.ecommerce_computer_components.persistence.entity.UserEntity;
import org.gad.ecommerce_computer_components.persistence.enums.AccountStatement;
import org.gad.ecommerce_computer_components.persistence.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.gad.ecommerce_computer_components.configuration.security.Constants.*;

@Component
public class JWTAuthorizationFilter extends OncePerRequestFilter {
    @Autowired
    private UserRepository userRepository;

    private Claims setSigningKey(HttpServletRequest request) {
        String jwtToken = request.getHeader(HEADER_AUTHORIZATION_KEY).replace(TOKEN_BEARER_PREFIX, "");
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey(SUPER_SECRET_KEY))
                .build()
                .parseClaimsJws(jwtToken)
                .getBody();
    }

    private void setAuthentication(Claims claims, UserEntity entity){
        List<GrantedAuthority> authorities;
        if(entity.getId() != null
                && entity.getRole().name() != null
                && entity.getAccountStatus().name() != null
                && !entity.getAccountStatus().name().equals(AccountStatement.SUSPENDIDO.name())){
            List<String> roles = Collections.singletonList(entity.getRole().name());
            authorities = roles.stream()
                    .map(rol -> new SimpleGrantedAuthority("ROLE_" + rol))
                    .collect(Collectors.toList());
        }else {
            authorities = Collections.emptyList();
        }

        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(claims.getSubject(), null, authorities);
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    private boolean isJWTValid(HttpServletRequest request, HttpServletResponse response){
        String authorizationHeader = request.getHeader(HEADER_AUTHORIZATION_KEY);
        if(authorizationHeader == null || !authorizationHeader.startsWith(TOKEN_BEARER_PREFIX)){
            return false;
        }
        return true;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        try {
            if(isJWTValid(request, response)){
                Claims claims = setSigningKey(request);
                String username = claims.getSubject();
                UserEntity user = userRepository.findByUsername(username);
                if(user != null){
                    setAuthentication(claims, user);
                }else {
                    SecurityContextHolder.clearContext();
                }
            }else {
                SecurityContextHolder.clearContext();
            }
            filterChain.doFilter(request, response);
        }catch (ExpiredJwtException | UnsupportedJwtException | MalformedJwtException e){
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.sendError(HttpServletResponse.SC_FORBIDDEN, e.getMessage());
        }
    }
}
