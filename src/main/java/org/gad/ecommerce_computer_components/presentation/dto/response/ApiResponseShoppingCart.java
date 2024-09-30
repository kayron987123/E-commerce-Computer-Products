package org.gad.ecommerce_computer_components.presentation.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.gad.ecommerce_computer_components.presentation.dto.ShoppingCartDTO;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ApiResponseShoppingCart {
    private int code;
    private String message;
    private List<ShoppingCartDTO> shoppingCartDTO;
}
