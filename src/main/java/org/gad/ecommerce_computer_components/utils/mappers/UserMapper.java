package org.gad.ecommerce_computer_components.utils.mappers;

import org.gad.ecommerce_computer_components.persistence.entity.UserEntity;
import org.gad.ecommerce_computer_components.presentation.dto.request.UserDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface UserMapper {
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    UserDTO userEntityToUserDTO(UserEntity userEntity);

    @Mapping(target = "creationDate", ignore = true)
    @Mapping(target = "updateDate", ignore = true)
    @Mapping(target = "role", source = "role", defaultValue = "USUARIO")
    @Mapping(target = "accountStatus", source = "accountStatus", defaultValue = "ACTIVO")
    @Mapping(target = "profileImage", defaultValue = "default.jpg")
    UserEntity userDTOToUserEntity(UserDTO userDTO);


}
