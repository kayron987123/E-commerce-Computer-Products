package org.gad.ecommerce_computer_components.presentation.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class UserRequest {
    @NotNull
    @NotBlank
    @Pattern(regexp = "^[A-Za-z0-9]+$", message = "Username should contain only numbers and letters")
    @Size(min = 8, message = "Username should have at least 8 characters")
    private String username;

    @NotNull
    @NotBlank
    @Pattern(regexp = "^[A-Za-z0-9]+$", message = "Password should contain only numbers and letters")
    @Size(min = 8, max = 20, message = "Password should have at least 8 characters and at most 20 characters")
    private String password;
}
