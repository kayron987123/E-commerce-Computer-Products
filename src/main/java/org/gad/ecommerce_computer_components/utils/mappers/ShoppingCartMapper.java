package org.gad.ecommerce_computer_components.utils.mappers;

import org.gad.ecommerce_computer_components.persistence.entity.ShoppingCart;
import org.gad.ecommerce_computer_components.presentation.dto.ShoppingCartDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ShoppingCartMapper {

    ShoppingCartMapper INSTANCE = Mappers.getMapper(ShoppingCartMapper.class);

    @Mapping(source = "user", target = "userId")
    @Mapping(source = "product", target = "productId")
    ShoppingCartDTO toDTO(ShoppingCart shoppingCart);

    @Mapping(source = "userId", target = "user")
    @Mapping(source = "productId", target = "product")
    ShoppingCart toEntity(ShoppingCartDTO shoppingCartDTO);


}
