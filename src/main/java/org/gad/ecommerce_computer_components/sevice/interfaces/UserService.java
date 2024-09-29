package org.gad.ecommerce_computer_components.sevice.interfaces;

import io.jsonwebtoken.Claims;
import org.gad.ecommerce_computer_components.persistence.entity.UserEntity;
import org.gad.ecommerce_computer_components.presentation.dto.UserDTO;
import org.gad.ecommerce_computer_components.presentation.dto.VerifyUserToken;
import org.springframework.security.core.userdetails.User;

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
