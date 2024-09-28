package org.gad.ecommerce_computer_components.presentation.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ApiResponseToken {
    private int code;
    private String message;
    private String token;
}
