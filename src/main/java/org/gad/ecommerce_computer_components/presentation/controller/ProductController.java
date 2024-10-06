package org.gad.ecommerce_computer_components.presentation.controller;

import org.gad.ecommerce_computer_components.presentation.dto.product.ProductDTO;
import org.gad.ecommerce_computer_components.presentation.dto.response.ApiResponseProduct;
import org.gad.ecommerce_computer_components.service.interfaces.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@RestController
@RequestMapping("/product")
@CrossOrigin
public class ProductController {
    private static final String INVALIDATION_MESSAGE = "Invalid File Type";
    private static final String FILE_PATH = "src/main/resources/static/images/";

    @Autowired
    private ProductService productService;

    @GetMapping("/list/")
    public ResponseEntity<ApiResponseProduct> getProducts(@RequestParam(value = "name", required = false) String name) {
        ApiResponseProduct response;

        if (name != null && !name.isEmpty()) {
            try {
                response = new ApiResponseProduct(true, "Product found", productService.findByProductName(name));
            } catch (Exception e) {
                response = new ApiResponseProduct(false, e.getMessage(), null);
            }
        } else {
            response = new ApiResponseProduct(true, "Products retrieved", productService.findAllProducts());
        }
        return ResponseEntity.ok(response);
    }

    @PostMapping("/save/")
    public ResponseEntity<ApiResponseProduct> createProduct(@RequestPart("product") ProductDTO productDTO,
                                                            @RequestPart(value = "image",required = false) MultipartFile image) {
        if (image != null && !image.isEmpty()) {
            String imageName = image.getOriginalFilename();
            if (imageName == null || !productService.ifImageIsValid(imageName)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponseProduct(false, INVALIDATION_MESSAGE,null));
            }
            try {
                Path path = Paths.get(FILE_PATH + imageName);
                Files.copy(image.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
                productDTO.setImage("/images/" + imageName);
            } catch (Exception e) {
                ApiResponseProduct response = new ApiResponseProduct(false, "Error creating product: " + e.getMessage(), null);
                return ResponseEntity.badRequest().body(response);
            }
        }
        ProductDTO savedProduct = productService.saveProduct(productDTO);
        ApiResponseProduct response = new ApiResponseProduct(true, "Product created successfully", savedProduct);
        return ResponseEntity.ok(response);
    }

    // 3. PUT: Actualizar un producto existente
    @PutMapping("/update/{id}")
    public ResponseEntity<ApiResponseProduct> updateProduct(@PathVariable Long id, @RequestBody ProductDTO productDTO) {
        try {
            // Verificar que el producto a actualizar tiene el ID correcto
            productDTO.setId(id);
            ProductDTO updatedProduct = productService.updateProduct(productDTO);
            ApiResponseProduct response = new ApiResponseProduct(true, "Product updated successfully", updatedProduct);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            ApiResponseProduct response = new ApiResponseProduct(false, "Error updating product: " + e.getMessage(), null);
            return ResponseEntity.badRequest().body(response);
        }
    }

    // 4. DELETE: Eliminar o desactivar un producto
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<ApiResponseProduct> deleteProduct(@PathVariable Long id) {
        try {
            ProductDTO deletedProduct = productService.deleteProduct(id);
            ApiResponseProduct response = new ApiResponseProduct(true, "Product deleted successfully", deletedProduct);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            ApiResponseProduct response = new ApiResponseProduct(false, "Error deleting product: " + e.getMessage(), null);
            return ResponseEntity.badRequest().body(response);
        }
    }
}
