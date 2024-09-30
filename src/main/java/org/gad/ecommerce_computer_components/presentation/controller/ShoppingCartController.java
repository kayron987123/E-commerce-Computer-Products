package org.gad.ecommerce_computer_components.presentation.controller;

import io.jsonwebtoken.Claims;
import org.gad.ecommerce_computer_components.presentation.dto.ShoppingCartDTO;
import org.gad.ecommerce_computer_components.presentation.dto.response.ApiResponse;
import org.gad.ecommerce_computer_components.presentation.dto.response.ApiResponseShoppingCart;
import org.gad.ecommerce_computer_components.sevice.interfaces.ShoppingCartService;
import org.gad.ecommerce_computer_components.sevice.interfaces.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.gad.ecommerce_computer_components.persistence.enums.ProductStatus.*;

@RestController
@RequestMapping("/shopping-carts")
public class ShoppingCartController {

    @Autowired
    private ShoppingCartService shoppingCartService;

    @Autowired
    private UserService userService;

    @PostMapping("/addProduct/cart")
    public ResponseEntity<ApiResponse> addProdductToCart(@RequestHeader(value = "Authorization") String authorizationHeader,
                                                         @RequestBody ShoppingCartDTO shoppingCartDTO) {
        try {
            if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
                String tokenJWT = authorizationHeader.substring(7);
                Claims claims = userService.extractClaimsFromJWT(tokenJWT);
                Long idUser = claims.get("id", Long.class);
                String role = claims.get("role", String.class);
                if (shoppingCartDTO != null || shoppingCartDTO.getProductId() != null ||
                        role.equals(AGOTADO.name()) || role.equals(DESCONTINUADO.name())) {
                    shoppingCartService.addProductToCart(idUser, shoppingCartDTO);
                    return ResponseEntity.ok(new ApiResponse(HttpStatus.OK.value(), "Product added to cart successfully"));
                }
                return ResponseEntity.ok(new ApiResponse(HttpStatus.BAD_REQUEST.value(), "Invalid product"));
            }
            return ResponseEntity.ok(new ApiResponse(HttpStatus.BAD_REQUEST.value(), "Invalid token"));
        }catch (Exception e) {
            return ResponseEntity.ok(new ApiResponse(HttpStatus.BAD_REQUEST.value(), e.getMessage()));
        }
    }

    @GetMapping("/getListCarts/cart")
    public ResponseEntity<ApiResponseShoppingCart> getCart(@RequestHeader(value = "Authorization") String authorizationHeader){
        try {
            if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
                String tokenJWT = authorizationHeader.substring(7);
                Claims claims = userService.extractClaimsFromJWT(tokenJWT);
                Long idUser = claims.get("id", Long.class);
                List<ShoppingCartDTO> shoppingCartDTO = shoppingCartService.getCart(idUser);
                return ResponseEntity.ok(new ApiResponseShoppingCart(HttpStatus.OK.value(), "Shopping cart retrieved successfully", shoppingCartDTO));
            }
            return ResponseEntity.ok(new ApiResponseShoppingCart(HttpStatus.BAD_REQUEST.value(), "Invalid token", null));
        }catch (Exception e) {
            return ResponseEntity.ok(new ApiResponseShoppingCart(HttpStatus.BAD_REQUEST.value(), e.getMessage(), null));
        }
    }

    @DeleteMapping("/removeProduct/cart")
    public ResponseEntity<ApiResponse> removeCart(@RequestHeader(value = "Authorization") String authorizationHeader,
                                                  @RequestBody ShoppingCartDTO shoppingCartDTO) {
        try {
            if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
                String tokenJWT = authorizationHeader.substring(7);
                Claims claims = userService.extractClaimsFromJWT(tokenJWT);
                Long idUser = claims.get("id", Long.class);
                if (shoppingCartDTO != null){
                    shoppingCartService.removeProductFromCart(idUser, shoppingCartDTO);
                    return ResponseEntity.ok(new ApiResponse(HttpStatus.OK.value(), "Product removed from cart successfully"));
                }
                return ResponseEntity.ok(new ApiResponse(HttpStatus.BAD_REQUEST.value(), "Invalid product"));
            }
            return ResponseEntity.ok(new ApiResponse(HttpStatus.BAD_REQUEST.value(), "Invalid token"));
        }catch (Exception e) {
            return ResponseEntity.ok(new ApiResponse(HttpStatus.BAD_REQUEST.value(), e.getMessage()));
        }
    }

    @DeleteMapping("/clearCart")
    public ResponseEntity<ApiResponse> clearCart(@RequestHeader(value = "Authorization") String authorizationHeader){
        try {
            if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
                String tokenJWT = authorizationHeader.substring(7);
                Claims claims = userService.extractClaimsFromJWT(tokenJWT);
                Long idUser = claims.get("id", Long.class);
                shoppingCartService.clearCart(idUser);
                return ResponseEntity.ok(new ApiResponse(HttpStatus.OK.value(), "Shopping cart cleared successfully"));
            }
            return ResponseEntity.ok(new ApiResponse(HttpStatus.BAD_REQUEST.value(), "Invalid token"));
        }catch (Exception e) {
            return ResponseEntity.ok(new ApiResponse(HttpStatus.BAD_REQUEST.value(), e.getMessage()));
        }
    }

}
