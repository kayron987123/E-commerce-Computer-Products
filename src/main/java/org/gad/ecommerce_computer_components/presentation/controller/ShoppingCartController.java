package org.gad.ecommerce_computer_components.presentation.controller;

import jakarta.validation.Valid;
import org.gad.ecommerce_computer_components.persistence.enums.ProductStatus;
import org.gad.ecommerce_computer_components.presentation.dto.DtoReturn.ListShoppingCartDTO;
import org.gad.ecommerce_computer_components.presentation.dto.request.ShoppingCartDTO;
import org.gad.ecommerce_computer_components.presentation.dto.response.ApiResponse;
import org.gad.ecommerce_computer_components.presentation.dto.response.ApiResponseShoppingCart;
import org.gad.ecommerce_computer_components.service.interfaces.ProductService;
import org.gad.ecommerce_computer_components.service.interfaces.ShoppingCartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/shopping-carts")
public class ShoppingCartController {

    @Autowired
    private ShoppingCartService shoppingCartService;

    @Autowired
    private ProductService productService;

    @PostMapping("/addProduct/cart")
    public ResponseEntity<ApiResponse> addProdductToCart(@RequestHeader(value = "Authorization") String authorizationHeader,
                                                         @RequestBody @Valid ShoppingCartDTO shoppingCartDTO) {
        try {
            Long idUser = shoppingCartService.extractUserIdFromToken(authorizationHeader);
            if (idUser == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new ApiResponse(HttpStatus.UNAUTHORIZED.value(), "Invalid token"));
            }

            ProductStatus status = productService.getProductStatus(shoppingCartDTO.getProductId());
            if (status.equals(ProductStatus.DESCONTINUADO) || status.equals(ProductStatus.AGOTADO)) {
                return ResponseEntity.badRequest()
                        .body(new ApiResponse(HttpStatus.BAD_REQUEST.value(), "Invalid product status"));
            }

            if (shoppingCartDTO.getAmount() > productService.getProductStock(shoppingCartDTO.getProductId())) {
                return ResponseEntity.badRequest()
                        .body(new ApiResponse(HttpStatus.BAD_REQUEST.value(), "Insufficient stock"));
            }

            shoppingCartService.addProductToCart(idUser, shoppingCartDTO);
            return ResponseEntity.ok(new ApiResponse(HttpStatus.OK.value(), "Product added to cart successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage()));
        }
    }

    @GetMapping("/getListCarts/cart")
    public ResponseEntity<ApiResponseShoppingCart> getCart(@RequestHeader(value = "Authorization") String authorizationHeader) {
        try {
            Long idUser = shoppingCartService.extractUserIdFromToken(authorizationHeader);
            if (idUser == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new ApiResponseShoppingCart(HttpStatus.UNAUTHORIZED.value(), "Invalid token", null));
            }

            List<ListShoppingCartDTO> cartItems = shoppingCartService.getCart(idUser);
            return ResponseEntity.ok(new ApiResponseShoppingCart(HttpStatus.OK.value(), "Shopping cart retrieved successfully", cartItems));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponseShoppingCart(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage(), null));
        }
    }

    @DeleteMapping("/removeProduct/cart")
    public ResponseEntity<ApiResponse> removeCart(@RequestHeader(value = "Authorization") String authorizationHeader,
                                                  @RequestBody @Valid ShoppingCartDTO shoppingCartDTO) {
        try {
            Long idUser = shoppingCartService.extractUserIdFromToken(authorizationHeader);
            if (idUser == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new ApiResponse(HttpStatus.UNAUTHORIZED.value(), "Invalid token"));
            }

            shoppingCartService.removeProductFromCart(idUser, shoppingCartDTO);
            return ResponseEntity.ok(new ApiResponse(HttpStatus.OK.value(), "Product removed from cart successfully"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(HttpStatus.BAD_REQUEST.value(), e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage()));
        }
    }

    @DeleteMapping("/clearCart")
    public ResponseEntity<ApiResponse> clearCart(@RequestHeader(value = "Authorization") String authorizationHeader) {
        try {
            Long idUser = shoppingCartService.extractUserIdFromToken(authorizationHeader);
            if (idUser == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new ApiResponse(HttpStatus.UNAUTHORIZED.value(), "Invalid token"));
            }

            shoppingCartService.clearCart(idUser);
            return ResponseEntity.ok(new ApiResponse(HttpStatus.OK.value(), "Shopping cart cleared successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage()));
        }
    }
}
