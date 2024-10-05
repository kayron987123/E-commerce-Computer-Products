package org.gad.ecommerce_computer_components.presentation.dto.user;

import lombok.Data;

@Data
public class UserRecoverPassword {
    private String email;
    private String firstPassword;
    private String secondPassword;
}
