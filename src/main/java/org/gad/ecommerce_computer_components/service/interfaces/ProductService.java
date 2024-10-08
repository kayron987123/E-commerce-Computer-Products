package org.gad.ecommerce_computer_components.service.interfaces;

import org.gad.ecommerce_computer_components.persistence.enums.ProductStatus;

public interface ProductService {
    ProductStatus getProductStatus(Long id);
    void updateProductStock(Long productId, int quantity);
    void updateProductReturnStockRedis(Long productId, int quantity);
    Integer getProductStock(Long productId);
}
