package org.gad.ecommerce_computer_components.presentation.dto;

import lombok.Data;

@Data
public class ShoppingCartDTO {
    private Long id;
    private Long productId;
    private Integer amount;
}
