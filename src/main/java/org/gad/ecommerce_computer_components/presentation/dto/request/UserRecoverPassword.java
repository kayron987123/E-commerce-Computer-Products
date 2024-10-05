package org.gad.ecommerce_computer_components.presentation.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class UserRecoverPassword {
    @NotNull(message = "Email cannot be null")
    @NotBlank(message = "Email cannot be blank")
    @Email(message = "Email should be valid")
    @Size(min = 8, message = "Username should have at least 8 characters")
    private String email;

    @NotBlank
    @NotNull
    @Size(min = 8, max = 20, message = "Password should have at least 8 characters and at most 20 characters")
    @Pattern(regexp = "^[A-Za-z0-9]+$", message = "Password should contain only numbers and letters")
    private String firstPassword;

    @NotBlank
    @NotNull
    @Size(min = 8, max = 20, message = "Password should have at least 8 characters and at most 20 characters")
    @Pattern(regexp = "^[A-Za-z0-9]+$", message = "Password should contain only numbers and letters")
    private String secondPassword;
}
