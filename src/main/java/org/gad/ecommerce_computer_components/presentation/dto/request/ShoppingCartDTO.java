package org.gad.ecommerce_computer_components.presentation.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ShoppingCartDTO {
    private Long id;
    @NotNull(message = "User id cannot be null")
    @Min(value = 1, message = "Product id should be greater than 0")
    private Long productId;

    @NotNull(message = "Amount cannot be null")
    @Min(value = 1, message = "Amount should be greater than 0")
    private Integer amount;
}
