package org.gad.ecommerce_computer_components.sevice.interfaces;

import org.gad.ecommerce_computer_components.persistence.entity.UserEntity;
import org.gad.ecommerce_computer_components.presentation.dto.UserDTO;
import org.gad.ecommerce_computer_components.presentation.dto.UserRequest;
import org.gad.ecommerce_computer_components.presentation.dto.VerifyUserToken;

public interface UserService {
    UserEntity findByUsername(String username);
    String authenticateUser(String username, String password);
}
