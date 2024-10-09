package org.gad.ecommerce_computer_components.presentation.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderDTO {
    @NotNull
    @NotBlank
    @Size(min = 1, max = 100, message = "The shipping address must be between 1 and 100 characters")
    private String shippingAddress;
}
