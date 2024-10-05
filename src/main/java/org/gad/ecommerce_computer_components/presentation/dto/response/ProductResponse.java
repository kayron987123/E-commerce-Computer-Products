package org.gad.ecommerce_computer_components.presentation.dto.response;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ProductResponse {
    private boolean success;
    private String message;
    private Object data;
}
