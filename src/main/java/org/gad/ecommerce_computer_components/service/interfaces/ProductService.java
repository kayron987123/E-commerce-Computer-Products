package org.gad.ecommerce_computer_components.service.interfaces;

import org.gad.ecommerce_computer_components.presentation.dto.product.ProductDTO;

import java.util.List;

public interface ProductService {
    List<ProductDTO> findAllProducts();
    ProductDTO findById(long id);
    ProductDTO findByProductName(String name);
    ProductDTO saveProduct(ProductDTO productDTO);
    ProductDTO deleteProduct(long id);
    ProductDTO updateProduct(ProductDTO productDTO);
    boolean ifImageIsValid(String fileName);
}
