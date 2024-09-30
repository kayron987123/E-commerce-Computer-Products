package org.gad.ecommerce_computer_components.presentation.controller;

import org.gad.ecommerce_computer_components.persistence.enums.ProductStatus;
import org.gad.ecommerce_computer_components.presentation.dto.ListShoppingCartDTO;
import org.gad.ecommerce_computer_components.presentation.dto.ShoppingCartDTO;
import org.gad.ecommerce_computer_components.presentation.dto.response.ApiResponse;
import org.gad.ecommerce_computer_components.presentation.dto.response.ApiResponseShoppingCart;
import org.gad.ecommerce_computer_components.sevice.interfaces.ProductService;
import org.gad.ecommerce_computer_components.sevice.interfaces.ShoppingCartService;
import org.gad.ecommerce_computer_components.sevice.interfaces.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

import static org.gad.ecommerce_computer_components.persistence.enums.ProductStatus.*;

@RestController
@RequestMapping("/shopping-carts")
public class ShoppingCartController {

    @Autowired
    private ShoppingCartService shoppingCartService;

    @Autowired
    private UserService userService;

    @Autowired
    private ProductService productService;

    @PostMapping("/addProduct/cart")
    public ResponseEntity<ApiResponse> addProdductToCart(@RequestHeader(value = "Authorization") String authorizationHeader,
                                                         @RequestBody ShoppingCartDTO shoppingCartDTO) {
        try {
            Long idUser = shoppingCartService.extractUserIdFromToken(authorizationHeader);
            if (idUser != null) {
                ProductStatus status = productService.getProductStatus(shoppingCartDTO.getProductId());
                if (shoppingCartDTO != null || shoppingCartDTO.getProductId() != null || !status.equals(DESCONTINUADO.name()) || !status.equals(AGOTADO.name())) {
                    shoppingCartService.addProductToCart(idUser, shoppingCartDTO);
                    return ResponseEntity.ok(new ApiResponse(HttpStatus.OK.value(), "Product added to cart successfully"));
                }
                return ResponseEntity.ok(new ApiResponse(HttpStatus.BAD_REQUEST.value(), "Invalid product or status"));
            }
            return ResponseEntity.ok(new ApiResponse(HttpStatus.BAD_REQUEST.value(), "Invalid token"));
        } catch (Exception e) {
            return ResponseEntity.ok(new ApiResponse(HttpStatus.BAD_REQUEST.value(), e.getMessage()));
        }
    }

    @GetMapping("/getListCarts/cart")
    public ResponseEntity<ApiResponseShoppingCart> getCart(@RequestHeader(value = "Authorization") String authorizationHeader) {
        try {
            Long idUser = shoppingCartService.extractUserIdFromToken(authorizationHeader);
            if (idUser != null) {
                List<ListShoppingCartDTO> carItems = shoppingCartService.getCart(idUser);
                return ResponseEntity.ok(new ApiResponseShoppingCart(HttpStatus.OK.value(), "Shopping cart retrieved successfully", carItems));
            }
            return ResponseEntity.ok(new ApiResponseShoppingCart(HttpStatus.BAD_REQUEST.value(), "Invalid token", null));
        } catch (Exception e) {
            return ResponseEntity.ok(new ApiResponseShoppingCart(HttpStatus.BAD_REQUEST.value(), e.getMessage(), null));
        }
    }

    @DeleteMapping("/removeProduct/cart")
    public ResponseEntity<ApiResponse> removeCart(@RequestHeader(value = "Authorization") String authorizationHeader,
                                                  @RequestBody ShoppingCartDTO shoppingCartDTO) {
        try {
            Long idUser = shoppingCartService.extractUserIdFromToken(authorizationHeader);
            if (idUser != null) {
                if (shoppingCartDTO != null) {
                    shoppingCartService.removeProductFromCart(idUser, shoppingCartDTO);
                    return ResponseEntity.ok(new ApiResponse(HttpStatus.OK.value(), "Product removed from cart successfully"));
                }
                return ResponseEntity.ok(new ApiResponse(HttpStatus.BAD_REQUEST.value(), "Invalid product"));
            }
            return ResponseEntity.ok(new ApiResponse(HttpStatus.BAD_REQUEST.value(), "Invalid token"));
        } catch (Exception e) {
            return ResponseEntity.ok(new ApiResponse(HttpStatus.BAD_REQUEST.value(), e.getMessage()));
        }
    }

    @DeleteMapping("/clearCart")
    public ResponseEntity<ApiResponse> clearCart(@RequestHeader(value = "Authorization") String authorizationHeader) {
        try {
            Long idUser = shoppingCartService.extractUserIdFromToken(authorizationHeader);
            if (idUser != null) {
                shoppingCartService.clearCart(idUser);
                return ResponseEntity.ok(new ApiResponse(HttpStatus.OK.value(), "Shopping cart cleared successfully"));
            }
            return ResponseEntity.ok(new ApiResponse(HttpStatus.BAD_REQUEST.value(), "Invalid token"));
        } catch (Exception e) {
            return ResponseEntity.ok(new ApiResponse(HttpStatus.BAD_REQUEST.value(), e.getMessage()));
        }
    }

}
