package org.gad.ecommerce_computer_components.persistence.repository;

import io.lettuce.core.dynamic.annotation.Param;
import org.gad.ecommerce_computer_components.persistence.entity.Product;
import org.gad.ecommerce_computer_components.persistence.entity.ShoppingCart;
import org.gad.ecommerce_computer_components.persistence.enums.ProductStatus;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface ProductRepository extends CrudRepository<Product, Long> {

    @Query("SELECT p.status FROM Product p WHERE p.id = :id")
    ProductStatus findStatusById(@Param("id") Long id);
}
