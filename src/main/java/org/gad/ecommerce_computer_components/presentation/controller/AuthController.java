package org.gad.ecommerce_computer_components.presentation.controller;

import org.gad.ecommerce_computer_components.presentation.dto.ApiResponseToken;
import org.gad.ecommerce_computer_components.presentation.dto.AuthRequest;
import org.gad.ecommerce_computer_components.sevice.interfaces.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<ApiResponseToken> loginUser(@RequestBody AuthRequest authRequest) {
        try {
            String token = authService.authenticate(authRequest);
            if (token != null) {
                ApiResponseToken response = new ApiResponseToken(HttpStatus.OK.value(), "Successful authentication", token);
                return ResponseEntity.ok(response);
            }
        } catch (UsernameNotFoundException e) {
            ApiResponseToken response = new ApiResponseToken(HttpStatus.UNAUTHORIZED.value(), e.getMessage(), null);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponseToken(HttpStatus.BAD_REQUEST.value(), "Mala peticion", null));
    }
}
