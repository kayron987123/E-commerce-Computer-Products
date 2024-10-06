package org.gad.ecommerce_computer_components.utils.mappers;

import org.gad.ecommerce_computer_components.persistence.entity.Model;
import org.gad.ecommerce_computer_components.persistence.entity.Product;
import org.gad.ecommerce_computer_components.persistence.entity.ShoppingCart;
import org.gad.ecommerce_computer_components.persistence.entity.UserEntity;
import org.gad.ecommerce_computer_components.presentation.dto.DtoReturn.ListShoppingCartDTO;
import org.gad.ecommerce_computer_components.presentation.dto.DtoReturn.ProductShoppingCartDTO;
import org.gad.ecommerce_computer_components.presentation.dto.user.ShoppingCartDTO;
import org.gad.ecommerce_computer_components.presentation.dto.DtoReturn.UserShoppingCartDTO;
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

    // Nuevo mapeo para ListShoppingCartDTO
    @Mapping(source = "user", target = "user")
    @Mapping(source = "product", target = "product")
    ListShoppingCartDTO toListDTO(ShoppingCart shoppingCart);

    // Métodos para mapear entidades relacionadas
    @Mapping(source = "id", target = "id")
    @Mapping(source = "username", target = "username")
    @Mapping(source = "email", target = "email")
    @Mapping(source = "cellphone", target = "cellphone")
    UserShoppingCartDTO toUserDTO(UserEntity user);

    @Mapping(source = "id", target = "id")
    @Mapping(source = "image", target = "image")
    @Mapping(source = "name", target = "name")
    @Mapping(source = "price", target = "price")
    @Mapping(source = "stock", target = "stock")
    @Mapping(source = "model", target = "model")
    ProductShoppingCartDTO toProductDTO(Product product);

    // Conversión personalizada de Model a String
    default String map(Model model) {
        return model != null ? model.getName() : null;
    }

    // Conversión personalizada de String a Model
    default Model map(String modelName) {
        if (modelName != null) {
            Model model = new Model();
            model.setName(modelName);
            return model;
        }
        return null;
    }

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