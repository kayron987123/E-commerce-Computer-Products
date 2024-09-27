package org.gad.ecommerce_computer_components.sevice.impl;

import org.gad.ecommerce_computer_components.configuration.security.JWTAuthenticationConfig;
import org.gad.ecommerce_computer_components.persistence.entity.UserEntity;
import org.gad.ecommerce_computer_components.persistence.repository.UserRepository;
import org.gad.ecommerce_computer_components.presentation.dto.AuthRequest;
import org.gad.ecommerce_computer_components.sevice.interfaces.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JWTAuthenticationConfig jwtAuthenticationConfig;

    @Override
    public UserEntity findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public String authenticate(AuthRequest authRequest) throws UsernameNotFoundException {
        UserEntity userResult = userRepository.findByUsername(authRequest.getUsername());
        if (userResult == null) {
            throw new UsernameNotFoundException("User not found");
        }
        if (new BCryptPasswordEncoder().matches(authRequest.getPassword(), userResult.getPassword())) {
            return jwtAuthenticationConfig.getJWTToken(userResult);
        }
        throw new UsernameNotFoundException("Invalid credentials");
    }
}
