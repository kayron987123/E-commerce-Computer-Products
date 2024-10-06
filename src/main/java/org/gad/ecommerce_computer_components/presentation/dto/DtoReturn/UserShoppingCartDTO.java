package org.gad.ecommerce_computer_components.presentation.dto.DtoReturn;

import lombok.*;

@Getter
@Setter
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class UserShoppingCartDTO {
    private Long id;
    private String username;
    private String email;
    private String cellphone;
}