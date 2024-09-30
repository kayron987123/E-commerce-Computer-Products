package org.gad.ecommerce_computer_components.sevice.impl;

import org.gad.ecommerce_computer_components.persistence.entity.Product;
import org.gad.ecommerce_computer_components.persistence.enums.ProductStatus;
import org.gad.ecommerce_computer_components.persistence.repository.ProductRepository;
import org.gad.ecommerce_computer_components.sevice.interfaces.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Override
    public ProductStatus getProductStatus(Long id) {
        return productRepository.findStatusById(id);
    }

    @Override
    public void updateProductStock(Long productId, int quantity) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));

        int newStock = product.getStock() - quantity;
        if (newStock < 0){
            throw new IllegalArgumentException("Insufficient stock");
        }
        product.setStock(newStock);
        productRepository.save(product);
    }
}
