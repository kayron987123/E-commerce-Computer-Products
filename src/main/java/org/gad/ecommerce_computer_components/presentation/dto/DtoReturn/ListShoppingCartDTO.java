package org.gad.ecommerce_computer_components.presentation.dto.DtoReturn;

import lombok.*;

@Getter
@Setter
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class ListShoppingCartDTO {
    private Long id;
    private UserShoppingCartDTO user;
    private ProductShoppingCartDTO product;
    private Integer amount;
}
