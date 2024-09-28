package org.gad.ecommerce_computer_components.sevice.impl;

import jakarta.transaction.Transactional;
import org.gad.ecommerce_computer_components.configuration.security.JWTAuthenticationConfig;
import org.gad.ecommerce_computer_components.persistence.entity.UserEntity;
import org.gad.ecommerce_computer_components.persistence.repository.UserRepository;
import org.gad.ecommerce_computer_components.presentation.dto.UserDTO;
import org.gad.ecommerce_computer_components.presentation.dto.UserRequest;
import org.gad.ecommerce_computer_components.presentation.dto.VerifyUserToken;
import org.gad.ecommerce_computer_components.sevice.interfaces.EmailService;
import org.gad.ecommerce_computer_components.sevice.interfaces.UserService;
import org.gad.ecommerce_computer_components.utils.mappers.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.Duration;

@Service
public class UserServiceImpl implements UserService {

    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final int TOKEN_LENGTH = 10;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JWTAuthenticationConfig jwtAuthenticationConfig;

    @Override
    public UserEntity findByUsername(String username) {
        return this.userRepository.findByUsername(username);
    }

    @Transactional
    @Override
    public String authenticateUser(String username, String password) throws UsernameNotFoundException {
        UserEntity userResult = this.userRepository.findByUsername(username);
        if (userResult == null) {
            throw new UsernameNotFoundException("User not found");
        }
        if (new BCryptPasswordEncoder().matches(password, userResult.getPassword())) {
            return jwtAuthenticationConfig.getJWTToken(userResult);
        }
        throw new UsernameNotFoundException("Invalid credentials");
    }
}
