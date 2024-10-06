package org.gad.ecommerce_computer_components.service.interfaces;

import io.jsonwebtoken.Claims;
import org.gad.ecommerce_computer_components.presentation.dto.user.UserDTO;
import org.gad.ecommerce_computer_components.presentation.dto.user.VerifyUserToken;

import java.util.Optional;

public interface UserService {
    UserDTO findByUsername(String username);
    String authenticateUser(String username, String password);


    void saveUserInRedis(UserDTO userDTO, String emailKey);
    String generateToken();
    String verifyUserToken(VerifyUserToken verifyUserToken);
    UserDTO saveUser(UserDTO userDTO);
    boolean isImageFile(String fileName);

    UserDTO findByEmail(String email);

    Claims extractClaimsFromJWT(String tokenJWT);
    UserDTO deleteUser(String email);
    Optional<UserDTO> findById(Long id);
}
