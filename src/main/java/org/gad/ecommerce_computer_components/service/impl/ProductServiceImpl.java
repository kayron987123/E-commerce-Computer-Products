package org.gad.ecommerce_computer_components.service.impl;

import jakarta.transaction.Transactional;
import org.gad.ecommerce_computer_components.persistence.entity.Product;
import org.gad.ecommerce_computer_components.persistence.enums.ProductStatus;
import org.gad.ecommerce_computer_components.persistence.repository.ProductRepository;
import org.gad.ecommerce_computer_components.service.interfaces.ProductService;
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

    @Transactional
    @Override
    public void updateProductReturnStockRedis(Long productId, int quantity) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));

        int newStock = product.getStock() + quantity;
        product.setStock(newStock);
        productRepository.save(product);
    }

    @Override
    public Integer getProductStock(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));
        return product.getStock();
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
