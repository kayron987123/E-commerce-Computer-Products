package org.gad.ecommerce_computer_components.presentation.dto.response;

import lombok.*;
import org.gad.ecommerce_computer_components.persistence.entity.Product;
import org.gad.ecommerce_computer_components.presentation.dto.request.ProductDTO;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ApiResponseProduct {
    private boolean success;
    private String message;
    private List<ProductDTO> data;
    private ProductDTO productDTO;
}
