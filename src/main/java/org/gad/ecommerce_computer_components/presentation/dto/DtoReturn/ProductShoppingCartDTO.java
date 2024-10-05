package org.gad.ecommerce_computer_components.presentation.dto.DtoReturn;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class ProductShoppingCartDTO {
    private Long id;
    private String image;
    private String name;
    private BigDecimal price;
    private Integer stock;
    private String model;
}
