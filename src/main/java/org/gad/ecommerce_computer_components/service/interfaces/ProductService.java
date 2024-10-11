package org.gad.ecommerce_computer_components.service.interfaces;

import org.gad.ecommerce_computer_components.persistence.enums.ProductStatus;
import org.gad.ecommerce_computer_components.presentation.dto.request.ProductDTO;

import java.util.List;

public interface ProductService {
    List<ProductDTO> findAllProducts();
    ProductDTO findById(long id);
    ProductDTO findByProductName(String name);
    ProductDTO saveProduct(ProductDTO productDTO);
    ProductDTO deleteProduct(long id);
    ProductDTO updateProduct(ProductDTO productDTO);
    boolean ifImageIsValid(String fileName);
    ProductStatus getProductStatus(Long id);
    void updateProductStock(Long productId, int quantity);
    void updateProductReturnStockRedis(Long productId, int quantity);
    Integer getProductStock(Long productId);
}
