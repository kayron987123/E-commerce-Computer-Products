package org.gad.ecommerce_computer_components.presentation.dto.response;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ApiResponseProduct {
    private boolean success;
    private String message;
    private Object data;
}
