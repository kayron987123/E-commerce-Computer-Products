package org.gad.ecommerce_computer_components.utils.mappers;

import org.gad.ecommerce_computer_components.persistence.entity.Model;
import org.gad.ecommerce_computer_components.persistence.entity.Product;
import org.gad.ecommerce_computer_components.presentation.dto.ProductShoppingCartDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ProductMapper {
    ProductMapper INSTANCE = Mappers.getMapper(ProductMapper.class);

    @Mapping(source = "model", target = "model")
    ProductShoppingCartDTO toDTO(Product product);

    @Mapping(source = "model", target = "model")
    Product toEntity(ProductShoppingCartDTO productDTO);

    default String map(Model model){
        return model != null ? model.getName() : null;
    }

    default Model map(String modelName){
        if (modelName != null) {
            Model model = new Model();
            model.setName(modelName);
            return model;
        }
        return null;
    }
}
