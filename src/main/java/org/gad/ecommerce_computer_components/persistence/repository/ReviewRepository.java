package org.gad.ecommerce_computer_components.persistence.repository;

import org.gad.ecommerce_computer_components.persistence.entity.Review;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface ReviewRepository extends CrudRepository<Review, Long> {
}
