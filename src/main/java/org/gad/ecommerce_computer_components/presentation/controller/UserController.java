package org.gad.ecommerce_computer_components.presentation.controller;

import jakarta.mail.Multipart;
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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@RestController
@RequestMapping("/users")
public class UserController {
    private static final String INVALIDATION_MESSAGE = "Invalid File Type";
    private static final String FILE_PATH = "src/main/resources/static/images/";

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
    public ResponseEntity<ApiResponse> registerUser(@RequestPart("user") UserDTO userDTO,
                                                    @RequestPart(value = "file", required = false) MultipartFile file) {

        if (userDTO != null) {
            if (file != null && !file.isEmpty()) {
                String fileName = file.getOriginalFilename();
                if (fileName == null || !userService.isImageFile(fileName)) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse(HttpStatus.BAD_REQUEST.value(), "Invalid File Type"));
                }
                try {
                    Path path = Paths.get(FILE_PATH + fileName);
                    Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
                    userDTO.setProfileImage("/images/" + fileName);
                } catch (IOException e) {
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Internal Server Error"));
                }
            }
            userService.saveUserInRedis(userDTO);
            return ResponseEntity.ok(new ApiResponse(HttpStatus.OK.value(), "User registered successfully"));
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse(HttpStatus.BAD_REQUEST.value(), "Bad Request"));
    }

    @PostMapping("/verifyToken/user")
    public ResponseEntity<ApiResponseToken> verifyTokenUser(@RequestBody VerifyUserToken verifyUserToken) {

        if(verifyUserToken != null){
            String token = userService.verifyUserToken(verifyUserToken);
            if(token != null){
                return ResponseEntity.ok(new ApiResponseToken(HttpStatus.OK.value(), "Token verified successfully", token));
            }
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponseToken(HttpStatus.UNAUTHORIZED.value(), "Token not verified", null));
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponseToken(HttpStatus.BAD_REQUEST.value(), "Bad Request", null));
    }
}
