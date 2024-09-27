package org.gad.ecommerce_computer_components.sevice.interfaces;

import org.gad.ecommerce_computer_components.persistence.entity.UserEntity;
import org.gad.ecommerce_computer_components.presentation.dto.AuthRequest;

public interface AuthService {
    UserEntity findByUsername(String username);
    String authenticate(AuthRequest authRequest);
}
