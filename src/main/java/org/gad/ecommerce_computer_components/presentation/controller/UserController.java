package org.gad.ecommerce_computer_components.presentation.controller;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import org.gad.ecommerce_computer_components.persistence.enums.AccountStatement;
import org.gad.ecommerce_computer_components.presentation.dto.*;
import org.gad.ecommerce_computer_components.presentation.dto.response.ApiResponse;
import org.gad.ecommerce_computer_components.presentation.dto.response.ApiResponseToken;
import org.gad.ecommerce_computer_components.sevice.interfaces.CartTransferService;
import org.gad.ecommerce_computer_components.sevice.interfaces.ShoppingCartService;
import org.gad.ecommerce_computer_components.sevice.interfaces.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Optional;

import static org.gad.ecommerce_computer_components.persistence.enums.AccountStatement.ACTIVO;
import static org.gad.ecommerce_computer_components.persistence.enums.AccountStatement.ELIMINADO;
import static org.gad.ecommerce_computer_components.persistence.enums.Role.ADMINISTRADOR;

@RestController
@RequestMapping("/users")
public class UserController {
    private static final String INVALIDATION_MESSAGE = "Invalid File Type";
    private static final String FILE_PATH = "src/main/resources/static/images/";
    private static final String EMAIL_SEND_TOKEN = "TOKEN";
    private static final String EMAIL_UPDATE_PASSWORD = "UPDATE";

    @Autowired
    private UserService userService;

    @Autowired
    private CartTransferService cartTransferService;

    @Autowired
    private ShoppingCartService shoppingCartService;

    @PostMapping("/login/user")
    public ResponseEntity<ApiResponseToken> loginUser(@RequestBody UserRequest userRequest,
                                                      @RequestParam String tempCartId) {
        try {
            UserDTO userDTO = userService.findByUsername(userRequest.getUsername());
            if (userDTO.getAccountStatus().name().equals(ELIMINADO.name())) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new ApiResponseToken(HttpStatus.UNAUTHORIZED.value(), "Cannot authenticate a deleted user.", null));
            }

            String token = userService.authenticateUser(userRequest.getUsername(), userRequest.getPassword());

            if (tempCartId != null && !tempCartId.isEmpty()) {
                Long userId = shoppingCartService.extractUserIdFromToken(token);
                cartTransferService.transferTempCartToUserCart(tempCartId, userId);
            }

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
    public ResponseEntity<ApiResponseToken> verifyTokenUser(@RequestBody VerifyUserToken verifyUserToken,
                                                            @RequestParam(required = false) String tempCartId) {
        String token = userService.verifyUserToken(verifyUserToken);

        if (tempCartId != null && !tempCartId.isEmpty()) {
            Long userId = shoppingCartService.extractUserIdFromToken(token);
            cartTransferService.transferTempCartToUserCart(tempCartId, userId);
        }

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

    @PutMapping("/update/user")
    public ResponseEntity<ApiResponse> updateUser(@RequestHeader(value = "Authorization") String authorizationHeader,
                                                  @RequestPart(value = "user", required = false) UserDTO userDTO,
                                                  @RequestPart(value = "file", required = false) MultipartFile file) {
        try {
            if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
                String tokenJWT = authorizationHeader.substring(7);
                Claims claims = userService.extractClaimsFromJWT(tokenJWT);
                String email = claims.get("email", String.class);
                UserDTO userFinded = userService.findByEmail(email);

                if (userFinded == null) {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .body(new ApiResponse(HttpStatus.NOT_FOUND.value(), "User not found"));
                }

                // Actualización de la imagen, si se proporciona
                if (file != null && !file.isEmpty()) {
                    String fileName = file.getOriginalFilename();
                    if (fileName == null || !userService.isImageFile(fileName)) {
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                .body(new ApiResponse(HttpStatus.BAD_REQUEST.value(), "Invalid image file"));
                    }
                    try {
                        Path path = Paths.get(FILE_PATH + fileName);
                        Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
                        userFinded.setProfileImage("/images/" + fileName);
                    } catch (IOException e) {
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .body(new ApiResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Error saving image"));
                    }
                }

                // Actualización de los datos del UserDTO, si se proporciona
                if (userDTO != null) {
                    if (userDTO.getName() != null) userFinded.setName(userDTO.getName());
                    if (userDTO.getLastName() != null) userFinded.setLastName(userDTO.getLastName());
                    if (userDTO.getUsername() != null) userFinded.setUsername(userDTO.getUsername());
                    if (userDTO.getEmail() != null) userFinded.setEmail(userDTO.getEmail());
                    if (userDTO.getPassword() != null) userFinded.setPassword(userDTO.getPassword());
                    if (userDTO.getAddress() != null) userFinded.setAddress(userDTO.getAddress());
                    if (userDTO.getCellphone() != null) userFinded.setCellphone(userDTO.getCellphone());
                    if (userDTO.getDni() != null) userFinded.setDni(userDTO.getDni());
                }

                // Guarda los cambios en la base de datos
                userService.saveUser(userFinded);
                return ResponseEntity.ok(new ApiResponse(HttpStatus.OK.value(), "User updated successfully"));
            }

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse(HttpStatus.UNAUTHORIZED.value(), "Unauthorized"));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Error updating user: " + e.getMessage()));
        }
    }

    @PutMapping("/updateStatus/user/{id}")
    public ResponseEntity<ApiResponse> updateStatusUser(@RequestHeader(value = "Authorization") String authorizationHeader,
                                                        @PathVariable Long id) {
        try {
            if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
                String tokenJWT = authorizationHeader.substring(7);
                Claims claims = userService.extractClaimsFromJWT(tokenJWT);
                String role = claims.get("role", String.class);
                if (role.equals(ADMINISTRADOR.name())) {
                    Optional<UserDTO> userDTOOptional = userService.findById(id);
                    if (!userDTOOptional.isPresent()) {
                        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                .body(new ApiResponse(HttpStatus.NOT_FOUND.value(), "User not found"));
                    }
                    UserDTO currentUser = userDTOOptional.get();
                    currentUser.setAccountStatus(ACTIVO);
                    userService.saveUser(currentUser);
                    return ResponseEntity.ok(new ApiResponse(HttpStatus.OK.value(), "User status updated successfully"));
                }
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new ApiResponse(HttpStatus.UNAUTHORIZED.value(), "Action invalid due to its role"));
            }
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse(HttpStatus.UNAUTHORIZED.value(), "Unauthorized"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Error updating user status: " + e.getMessage()));
        }
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
