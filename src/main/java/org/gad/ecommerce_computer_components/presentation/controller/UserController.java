package org.gad.ecommerce_computer_components.presentation.controller;

import org.gad.ecommerce_computer_components.presentation.dto.UserDTO;
import org.gad.ecommerce_computer_components.presentation.dto.VerifyUserToken;
import org.gad.ecommerce_computer_components.presentation.dto.response.ApiResponse;
import org.gad.ecommerce_computer_components.presentation.dto.response.ApiResponseToken;
import org.gad.ecommerce_computer_components.presentation.dto.UserRequest;
import org.gad.ecommerce_computer_components.sevice.interfaces.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @PostMapping("/login/user")
    public ResponseEntity<ApiResponseToken> loginUser(@RequestBody UserRequest userRequest) {
        try {
            String token = userService.authenticateUser(userRequest.getUsername(), userRequest.getPassword());
            if (token != null) {
                ApiResponseToken response = new ApiResponseToken(HttpStatus.OK.value(), "Successful authentication", token);
                return ResponseEntity.ok(response);
            }
        } catch (UsernameNotFoundException e) {
            ApiResponseToken response = new ApiResponseToken(HttpStatus.UNAUTHORIZED.value(), e.getMessage(), null);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponseToken(HttpStatus.BAD_REQUEST.value(), "Bad Request", null));
    }



    @PostMapping("/register/user")
    public ResponseEntity<ApiResponse> registerUser(@RequestBody UserDTO userDTO) {
        return null;
    }

    @PostMapping("/verifyToken/user")
    public ResponseEntity<String> verifyTokenUser(@RequestBody VerifyUserToken verifyUserToken) {
        return null;
    }
}
