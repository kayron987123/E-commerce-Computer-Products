package org.gad.ecommerce_computer_components.presentation.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApiWithoutAuthResponseUUID {
    private int code;
    private String message;
    private String cartId;
}
