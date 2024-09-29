package org.gad.ecommerce_computer_components.presentation.controller;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import org.gad.ecommerce_computer_components.presentation.dto.*;
import org.gad.ecommerce_computer_components.presentation.dto.response.ApiResponse;
import org.gad.ecommerce_computer_components.presentation.dto.response.ApiResponseToken;
import org.gad.ecommerce_computer_components.sevice.interfaces.UserService;
import org.springframework.beans.factory.annotation.Autowired;
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
    private static final String EMAIL_SEND_TOKEN ="TOKEN";
    private static final String EMAIL_UPDATE_PASSWORD ="UPDATE";

    @Autowired
    private UserService userService;

    @PostMapping("/login/user")
    public ResponseEntity<ApiResponseToken> loginUser(@RequestBody UserRequest userRequest) {
        try {
            String token = userService.authenticateUser(userRequest.getUsername(), userRequest.getPassword());
            ApiResponseToken response = new ApiResponseToken(HttpStatus.OK.value(), "Successful authentication", token);
            return ResponseEntity.ok(response);
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponseToken(HttpStatus.UNAUTHORIZED.value(), e.getMessage(), null));
        }
    }


    @PostMapping("/register/user")
    public ResponseEntity<ApiResponse> registerUser(
            @RequestPart("user") UserDTO userDTO,
            @RequestPart(value = "file", required = false) MultipartFile file) {

        if (file != null && !file.isEmpty()) {
            String fileName = file.getOriginalFilename();
            if (fileName == null || !userService.isImageFile(fileName)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse(HttpStatus.BAD_REQUEST.value(), INVALIDATION_MESSAGE));
            }
            try {
                Path path = Paths.get(FILE_PATH + fileName);
                Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
                userDTO.setProfileImage("/images/" + fileName);
            } catch (IOException e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(new ApiResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Internal Server Error"));
            }
        }
        userService.saveUserInRedis(userDTO, EMAIL_SEND_TOKEN);
        return ResponseEntity.ok(new ApiResponse(HttpStatus.OK.value(), "User registered successfully and Token generated"));
    }

    @PostMapping("/verifyToken/user")
    public ResponseEntity<ApiResponseToken> verifyTokenUser(@RequestBody VerifyUserToken verifyUserToken) {
        String token = userService.verifyUserToken(verifyUserToken);
        if (token != null) {
            return ResponseEntity.ok(new ApiResponseToken(HttpStatus.OK.value(), "Token verified successfully", token));
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ApiResponseToken(HttpStatus.UNAUTHORIZED.value(), "Token not verified", null));
    }

    @PostMapping("/recoverPassword/user")
    public ResponseEntity<ApiResponse> recoverPassword(@RequestBody UserRecoverPassword userRecoverPassword) {
        if (userRecoverPassword != null &&
                userRecoverPassword.getFirstPassword().equals(userRecoverPassword.getSecondPassword())) {

            UserDTO userDTO = userService.findByEmail(userRecoverPassword.getEmail());
            if (userDTO != null) {
                userDTO.setPassword(userRecoverPassword.getFirstPassword());
                userService.saveUserInRedis(userDTO, EMAIL_UPDATE_PASSWORD);
                return ResponseEntity.ok(new ApiResponse(HttpStatus.OK.value(), "User found and Token generated"));
            }
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse(HttpStatus.BAD_REQUEST.value(), "Passwords do not match or invalid data"));
    }

    @DeleteMapping("/delete/user")
    public ResponseEntity<ApiResponse> deleteUser(@RequestHeader(value = "Authorization") String authorizationHeader) {
        try {
            if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
                String tokenJWT = authorizationHeader.substring(7);
                Claims claims = userService.extractClaimsFromJWT(tokenJWT);
                String email = claims.get("email", String.class);

                // Verificar que el email no sea nulo
                if (email == null) {
                    return ResponseEntity.badRequest().body(new ApiResponse(HttpStatus.BAD_REQUEST.value(), "Email not found in token"));
                }

                // Lógica para eliminar al usuario
                userService.deleteUser(email);
                return ResponseEntity.ok(new ApiResponse(HttpStatus.OK.value(), "User deleted successfully"));
            }
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse(HttpStatus.UNAUTHORIZED.value(), "Unauthorized"));
        } catch (ExpiredJwtException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse(HttpStatus.UNAUTHORIZED.value(), "Token has expired"));
        } catch (Exception e) {
            // Manejo de excepciones
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Error deleting user: " + e.getMessage()));
        }
    }
}
