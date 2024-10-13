package org.gad.ecommerce_computer_components.presentation.controller;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.validation.Valid;
import org.gad.ecommerce_computer_components.persistence.enums.AccountStatement;
import org.gad.ecommerce_computer_components.presentation.dto.request.UserDTO;
import org.gad.ecommerce_computer_components.presentation.dto.request.UserRecoverPassword;
import org.gad.ecommerce_computer_components.presentation.dto.request.UserRequest;
import org.gad.ecommerce_computer_components.presentation.dto.request.VerifyUserToken;
import org.gad.ecommerce_computer_components.presentation.dto.response.ApiResponse;
import org.gad.ecommerce_computer_components.presentation.dto.response.ApiResponseToken;
import org.gad.ecommerce_computer_components.service.interfaces.AzureBlobService;
import org.gad.ecommerce_computer_components.service.interfaces.CartTransferService;
import org.gad.ecommerce_computer_components.service.interfaces.ShoppingCartService;
import org.gad.ecommerce_computer_components.service.interfaces.UserService;
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
import java.util.Optional;

import static org.gad.ecommerce_computer_components.persistence.enums.Role.ADMINISTRADOR;

@CrossOrigin(origins = "http://localhost:3000")
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

    @Autowired
    private AzureBlobService azureBlobService;

    @PostMapping("/login/user")
    public ResponseEntity<ApiResponseToken> loginUser(@RequestBody @Valid UserRequest userRequest,
                                                      @RequestParam String tempCartId) {
        try {
            UserDTO userDTO = userService.findByUsername(userRequest.getUsername());
            if (userDTO.getAccountStatus().equals(AccountStatement.ELIMINADO)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new ApiResponseToken(HttpStatus.UNAUTHORIZED.value(), "Cannot authenticate a deleted user.", null));
            }

            String token = userService.authenticateUser(userRequest.getUsername(), userRequest.getPassword());

            // Transferir carrito temporal al usuario si existe tempCartId
            if (tempCartId != null && !tempCartId.isEmpty()) {
                Long userId = shoppingCartService.extractUserIdFromToken(token);
                cartTransferService.transferTempCartToUserCart(tempCartId, userId);
            }

            return ResponseEntity.ok(new ApiResponseToken(HttpStatus.OK.value(), "Successful authentication", token));
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponseToken(HttpStatus.UNAUTHORIZED.value(), e.getMessage(), null));
        }
    }


    @PostMapping("/register/user")
    public ResponseEntity<ApiResponse> registerUser(
            @RequestPart("user") @Valid UserDTO userDTO,
            @RequestPart(value = "file", required = false) MultipartFile file) {

        try {
            if (file != null && !file.isEmpty()) {
                try {
                    String fileUrl = azureBlobService.uploadFile(file);
                    userDTO.setProfileImage(fileUrl);
                } catch (IllegalArgumentException e) {
                    return ResponseEntity.badRequest()
                            .body(new ApiResponse(HttpStatus.BAD_REQUEST.value(), e.getMessage()));
                }
            }

            userService.saveUserInRedis(userDTO, EMAIL_SEND_TOKEN);
            return ResponseEntity.ok(new ApiResponse(HttpStatus.OK.value(), "User successfully registered and Token generated"));
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Error uploading file: " + e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Error registering user: " + e.getMessage()));
        }
    }

    @PutMapping("/update/user")
    public ResponseEntity<ApiResponse> updateUser(@RequestHeader(value = "Authorization") String authorizationHeader,
                                                  @RequestPart(value = "user", required = false) @Valid UserDTO userDTO,
                                                  @RequestPart(value = "file", required = false) MultipartFile file) {
        try {
            if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
                String tokenJWT = authorizationHeader.substring(7);
                Claims claims = userService.extractClaimsFromJWT(tokenJWT);
                String email = claims.get("email", String.class);
                UserDTO userFound = userService.findByEmail(email);

                if (userFound.getAccountStatus().equals(AccountStatement.ELIMINADO)) {
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                            .body(new ApiResponse(HttpStatus.UNAUTHORIZED.value(), "Cannot update a deleted user."));
                }

                if (userFound == null) {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .body(new ApiResponse(HttpStatus.NOT_FOUND.value(), "User not found"));
                }

                // Actualización de la imagen si se proporciona
                if (file != null && !file.isEmpty()) {
                    try {
                        String fileUrl = azureBlobService.uploadFile(file);
                        userFound.setProfileImage(fileUrl);
                    } catch (IllegalArgumentException e) {
                        return ResponseEntity.badRequest()
                                .body(new ApiResponse(HttpStatus.BAD_REQUEST.value(), "Invalid image file: " + e.getMessage()));
                    }
                }

                // Actualización de los datos del usuario
                if (userDTO != null) {
                    if (userDTO.getName() != null) userFound.setName(userDTO.getName());
                    if (userDTO.getLastName() != null) userFound.setLastName(userDTO.getLastName());
                    if (userDTO.getUsername() != null) userFound.setUsername(userDTO.getUsername());
                    if (userDTO.getEmail() != null) userFound.setEmail(userDTO.getEmail());
                    if (userDTO.getPassword() != null) userFound.setPassword(userDTO.getPassword());
                    if (userDTO.getAddress() != null) userFound.setAddress(userDTO.getAddress());
                    if (userDTO.getCellphone() != null) userFound.setCellphone(userDTO.getCellphone());
                    if (userDTO.getDni() != null) userFound.setDni(userDTO.getDni());
                }

                userService.saveUser(userFound);
                return ResponseEntity.ok(new ApiResponse(HttpStatus.OK.value(), "User updated successfully"));
            }

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse(HttpStatus.UNAUTHORIZED.value(), "Unauthorized"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Error updating user: " + e.getMessage()));
        }
    }

    @PostMapping("/verifyToken/user")
    public ResponseEntity<ApiResponseToken> verifyTokenUser(@RequestBody @Valid VerifyUserToken verifyUserToken,
                                                            @RequestParam(required = false) String tempCartId) {
        try {
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
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponseToken(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage(), null));
        }
    }

    @PostMapping("/recoverPassword/user")
    public ResponseEntity<ApiResponse> recoverPassword(@RequestBody @Valid UserRecoverPassword userRecoverPassword) {
        try {
            if (!userRecoverPassword.getFirstPassword().equals(userRecoverPassword.getSecondPassword())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ApiResponse(HttpStatus.BAD_REQUEST.value(), "Passwords do not match"));
            }

            UserDTO userDTO = userService.findByEmail(userRecoverPassword.getEmail());
            if (userDTO != null) {
                userDTO.setPassword(userRecoverPassword.getFirstPassword());
                userService.saveUserInRedis(userDTO, EMAIL_UPDATE_PASSWORD);
                return ResponseEntity.ok(new ApiResponse(HttpStatus.OK.value(), "Password updated successfully"));
            }

            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse(HttpStatus.NOT_FOUND.value(), "User not found"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage()));
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

                if (!role.equals(ADMINISTRADOR.name())) {
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                            .body(new ApiResponse(HttpStatus.UNAUTHORIZED.value(), "Action invalid due to its role"));
                }

                Optional<UserDTO> userDTOOptional = userService.findById(id);
                if (!userDTOOptional.isPresent()) {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .body(new ApiResponse(HttpStatus.NOT_FOUND.value(), "User not found"));
                }

                UserDTO currentUser = userDTOOptional.get();
                currentUser.setAccountStatus(AccountStatement.ACTIVO);
                userService.saveUser(currentUser);
                return ResponseEntity.ok(new ApiResponse(HttpStatus.OK.value(), "User status updated successfully"));
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

                if (email == null) {
                    return ResponseEntity.badRequest()
                            .body(new ApiResponse(HttpStatus.BAD_REQUEST.value(), "Email not found in token"));
                }

                userService.deleteUser(email);
                return ResponseEntity.ok(new ApiResponse(HttpStatus.OK.value(), "User deleted successfully"));
            }

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse(HttpStatus.UNAUTHORIZED.value(), "Unauthorized"));
        } catch (ExpiredJwtException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse(HttpStatus.UNAUTHORIZED.value(), "Token has expired"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Error deleting user: " + e.getMessage()));
        }
    }

}
