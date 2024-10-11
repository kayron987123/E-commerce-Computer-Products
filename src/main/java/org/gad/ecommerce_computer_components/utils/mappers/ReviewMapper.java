package org.gad.ecommerce_computer_components.utils.mappers;

import org.gad.ecommerce_computer_components.persistence.entity.Review;
import org.gad.ecommerce_computer_components.presentation.dto.request.ReviewDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ReviewMapper {
    ReviewMapper INSTANCE = Mappers.getMapper(ReviewMapper.class);

    Review reviewDTOToReviewEntity(ReviewDTO reviewDTO);
}
