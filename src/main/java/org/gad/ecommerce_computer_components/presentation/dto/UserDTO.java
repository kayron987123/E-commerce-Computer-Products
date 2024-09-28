package org.gad.ecommerce_computer_components.presentation.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.gad.ecommerce_computer_components.persistence.enums.AccountStatement;
import org.gad.ecommerce_computer_components.persistence.enums.Role;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class UserDTO {
    private Long id;
    private String name;
    private String lastName;
    private String username;
    private String email;
    private String password;
    private String address;
    private String cellphone;
    private String profileImage;
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
