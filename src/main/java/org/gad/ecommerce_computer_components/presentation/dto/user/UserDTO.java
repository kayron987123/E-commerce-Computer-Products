package org.gad.ecommerce_computer_components.presentation.dto.user;

import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.gad.ecommerce_computer_components.persistence.enums.AccountStatement;
import org.gad.ecommerce_computer_components.persistence.enums.Role;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class UserDTO {
    private Long id;

    @NotNull
    @NotBlank
    private String name;

    @NotNull
    @NotBlank
    private String lastName;

    @NotNull
    @NotBlank
    @Pattern(regexp = "^[A-Za-z0-9]+$", message = "Username should contain only numbers and letters")
    @Size(min = 8, message = "Username should have at least 8 characters")
    private String username;

    @NotNull(message = "Email cannot be null")
    @NotBlank(message = "Email cannot be blank")
    @Email(message = "Email should be valid")
    @Size(min = 8, message = "Username should have at least 8 characters")
    private String email;

    @NotNull(message = "Password cannot be null")
    @NotBlank
    @Pattern(regexp = "^[A-Za-z0-9]+$", message = "Password should contain only numbers and letters")
    @Size(min = 8, max = 20, message = "Password should have at least 8 characters and at most 20 characters")
    private String password;

    @NotNull
    @NotBlank
    private String address;

    @NotNull
    @NotBlank
    @Size(min = 9, max = 9, message = "Cellphone should have exactly 9 characters")
    @Pattern(regexp = "^9[0-9]{8}$", message = "Cellphone should start with 9 and have exactly 9 digits")
    private String cellphone;

    private String profileImage;

    @NotNull
    @NotBlank
    @Size(min = 8, max = 8, message = "Dni should have exactly 8 characters")
    @Pattern(regexp = "^[0-9]+$", message = "Dni should contain only numbers")
    private String dni;

    private Role role;
    private AccountStatement accountStatus;
    private LocalDateTime creationDate;
    private LocalDateTime updateDate;

    public UserDTO(Long id, String name, String lastName, String username, String email, String password, String address, String cellphone, String dni, Role role, AccountStatement accountStatus) {
        this.id = id;
        this.name = name;
        this.lastName = lastName;
        this.username = username;
        this.email = email;
        this.password = password;
        this.address = address;
        this.cellphone = cellphone;
        this.dni = dni;
        this.role = role;
        this.accountStatus = accountStatus;
    }
}