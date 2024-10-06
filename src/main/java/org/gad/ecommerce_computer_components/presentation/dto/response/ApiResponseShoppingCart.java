package org.gad.ecommerce_computer_components.presentation.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.gad.ecommerce_computer_components.presentation.dto.DtoReturn.ListShoppingCartDTO;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ApiResponseShoppingCart {
    private int code;
    private String message;
    private List<ListShoppingCartDTO> listShoppingCartDTO;
}