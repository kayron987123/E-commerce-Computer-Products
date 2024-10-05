package org.gad.ecommerce_computer_components.presentation.dto.product;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.gad.ecommerce_computer_components.persistence.enums.ProductStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductDTO {
    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private String stock;
    private String image;
    private LocalDateTime creationDate;
    private LocalDateTime updateDate;
    private ProductStatus status;
    private String warranty;
    private String specs;
    private String compatibility;
}
