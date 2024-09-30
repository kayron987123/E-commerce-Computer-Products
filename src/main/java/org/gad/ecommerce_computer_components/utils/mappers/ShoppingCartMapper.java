package org.gad.ecommerce_computer_components.utils.mappers;

import org.gad.ecommerce_computer_components.persistence.entity.Product;
import org.gad.ecommerce_computer_components.persistence.entity.ShoppingCart;
import org.gad.ecommerce_computer_components.persistence.entity.UserEntity;
import org.gad.ecommerce_computer_components.presentation.dto.ShoppingCartDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ShoppingCartMapper {

    ShoppingCartMapper INSTANCE = Mappers.getMapper(ShoppingCartMapper.class);

    @Mapping(source = "product", target = "productId")
    ShoppingCartDTO toDTO(ShoppingCart shoppingCart);

    @Mapping(source = "productId", target = "product")
    ShoppingCart toEntity(ShoppingCartDTO shoppingCartDTO);

    default Long map(Product product) {
        return product != null ? product.getId() : null;
    }

    default Product mapProduct(Long productId) {
        if (productId != null) {
            return null;
        }
        Product product = new Product();
        product.setId(productId);
        return product;
    }
}
