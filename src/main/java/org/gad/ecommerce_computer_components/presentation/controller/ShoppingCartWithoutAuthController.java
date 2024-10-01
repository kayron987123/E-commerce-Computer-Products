package org.gad.ecommerce_computer_components.presentation.controller;

import org.gad.ecommerce_computer_components.persistence.enums.ProductStatus;
import org.gad.ecommerce_computer_components.presentation.dto.ListShoppingCartDTO;
import org.gad.ecommerce_computer_components.presentation.dto.ShoppingCartDTO;
import org.gad.ecommerce_computer_components.presentation.dto.response.ApiResponse;
import org.gad.ecommerce_computer_components.presentation.dto.response.ApiWithoutAuthResponseCartItems;
import org.gad.ecommerce_computer_components.presentation.dto.response.ApiWithoutAuthResponseUUID;
import org.gad.ecommerce_computer_components.sevice.interfaces.ProductService;
import org.gad.ecommerce_computer_components.sevice.interfaces.ShoppingCartWithoutAuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/noauth/shopping-carts")
public class ShoppingCartWithoutAuthController {

    @Autowired
    private ProductService productService;

    @Autowired
    private ShoppingCartWithoutAuthService shoppingCartWithoutAuthService;

    @PostMapping("/createTempCart/cart")
    public ResponseEntity<ApiWithoutAuthResponseUUID> createTempCart() {
        String cartId = shoppingCartWithoutAuthService.createTempCart();
        return ResponseEntity.status(HttpStatus.CREATED).body(new ApiWithoutAuthResponseUUID(201, "Cart created", cartId));
    }

    @PostMapping("/{cartId}/addProduct/cart")
    public ResponseEntity<ApiResponse> addProductToTempCart(@PathVariable String cartId,
                                                            @RequestBody ShoppingCartDTO shoppingCartDTO) {
        ProductStatus statusProduct = productService.getProductStatus(shoppingCartDTO.getProductId());
        if (statusProduct.equals(ProductStatus.DESCONTINUADO) || statusProduct.equals(ProductStatus.AGOTADO)) {
            return ResponseEntity.ok(new ApiResponse(HttpStatus.BAD_REQUEST.value(), "Invalid product or status"));
        }
        shoppingCartWithoutAuthService.addProductToTempCart(cartId, shoppingCartDTO);
        return ResponseEntity.ok(new ApiResponse(HttpStatus.OK.value(), "Product added to cart successfully"));
    }

    @GetMapping("/{cartId}/getTempCartItems/cart")
    public ResponseEntity<ApiWithoutAuthResponseCartItems> getTempCartItems(@PathVariable String cartId) {
        List<ListShoppingCartDTO> cartItems = shoppingCartWithoutAuthService.getTempCartItems(cartId);
        return ResponseEntity.status(HttpStatus.OK).body(new ApiWithoutAuthResponseCartItems(200, "Cart items retrieved successfully", cartItems));
    }

    @DeleteMapping("/{cartId}/removeProduct/cart")
    public ResponseEntity<ApiResponse> removeProductFromCart(@PathVariable String cartId,
                                                             @RequestBody ShoppingCartDTO shoppingCartDTO) {
        shoppingCartWithoutAuthService.removeProductFromCart(cartId, shoppingCartDTO);
        return ResponseEntity.ok(new ApiResponse(HttpStatus.OK.value(), "Product removed from cart successfully"));
    }

    @DeleteMapping("/{cartId}/clearTempCart/cart")
    public ResponseEntity<ApiResponse> clearTempCart(@PathVariable String cartId) {
        shoppingCartWithoutAuthService.clearTempCart(cartId);
        return ResponseEntity.ok(new ApiResponse(HttpStatus.OK.value(), "Cart deleted successfully"));
    }


}
