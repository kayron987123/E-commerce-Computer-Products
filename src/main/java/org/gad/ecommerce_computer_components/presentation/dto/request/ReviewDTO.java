package org.gad.ecommerce_computer_components.presentation.dto.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReviewDTO {
    @NotNull(message = "Qualification cannot be null")
    @Min(value = 0, message = "Qualification should be at least 0")
    @Max(value = 5, message = "Qualification should be at most 5")
    private Integer qualification;

    @NotNull(message = "Comment cannot be null")
    @NotBlank(message = "Comment cannot be blank")
    @Size(min = 1, max = 255, message = "Comment should have at least 1 character and at most 255 characters")
    private String comment;
}
