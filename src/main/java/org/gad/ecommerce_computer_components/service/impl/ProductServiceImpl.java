package org.gad.ecommerce_computer_components.service.impl;

import jakarta.transaction.Transactional;
import org.gad.ecommerce_computer_components.persistence.entity.Product;
import org.gad.ecommerce_computer_components.persistence.enums.ProductStatus;
import org.gad.ecommerce_computer_components.persistence.repository.ProductRepository;
import org.gad.ecommerce_computer_components.presentation.dto.request.ProductDTO;
import org.gad.ecommerce_computer_components.service.interfaces.ProductService;
import org.gad.ecommerce_computer_components.utils.mappers.ProductMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {

    private static final List<String> VALID_EXTENSIONS = Arrays.asList("jpg", "jpeg", "png");

    @Autowired
    private ProductRepository productRepository;

    @Override
    public ProductStatus getProductStatus(Long id) {
        return productRepository.findStatusById(id);
    }

    @Override
    public List<ProductDTO> findAllProducts() {
        List<Product> products = (List<Product>) productRepository.findAll();
        return products.stream()
                .map(ProductMapper.INSTANCE::productToProductDTO)
                .toList();
    }

    @Override
    public ProductDTO findById(long id) {
        Product product = productRepository.findById(id).orElseThrow(() -> new RuntimeException("Product not found"));
        return ProductMapper.INSTANCE.productToProductDTO(product);
    }

    @Override
    public ProductDTO findByProductName(String productName) {
        Product product = productRepository.findByName(productName);
        if (product == null) {
            throw new RuntimeException("Product with name " + productName + " not found");
        }
        return ProductMapper.INSTANCE.productToProductDTO(product);
    }

    @Override
    @Transactional
    public ProductDTO saveProduct(ProductDTO productDTO) {
        // Validar la imagen antes de guardar
        if (productDTO.getImage() != null && !ifImageIsValid(productDTO.getImage())) {
            throw new IllegalArgumentException("Invalid image format");
        }

        // Validar otros campos si es necesario
        if (productDTO.getName() == null || productDTO.getName().isEmpty()) {
            throw new IllegalArgumentException("Product name cannot be null or empty");
        }

        Product product = ProductMapper.INSTANCE.productDTOToProduct(productDTO);
        Product savedProduct = productRepository.save(product);

        return ProductMapper.INSTANCE.productToProductDTO(savedProduct);
    }

    @Override
    public ProductDTO deleteProduct(long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        product.setStatus(ProductStatus.DESCONTINUADO);
        productRepository.save(product);

        return ProductMapper.INSTANCE.productToProductDTO(product);
    }

    @Override
    public ProductDTO updateProduct(ProductDTO productDTO) {
        Product existingProduct = productRepository.findById(productDTO.getId())
                .orElseThrow(() -> new RuntimeException("Product not found"));

        Product updatedProduct = ProductMapper.INSTANCE.productDTOToProduct(productDTO);
        updatedProduct.setId(existingProduct.getId());
        updatedProduct.setCreationDate(existingProduct.getCreationDate());
        updatedProduct = productRepository.save(updatedProduct);

        return ProductMapper.INSTANCE.productToProductDTO(updatedProduct);
    }

    @Override
    public boolean ifImageIsValid(String fileName) {
        String fileExtension = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
        return VALID_EXTENSIONS.contains(fileExtension);
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
