package org.gad.ecommerce_computer_components.persistence.repository;

import org.gad.ecommerce_computer_components.persistence.entity.Product;
import org.gad.ecommerce_computer_components.persistence.enums.ProductStatus;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends CrudRepository<Product, Long> {
    Product findByName(String name);

    @Query("SELECT p.status FROM Product p WHERE p.id = :id")
    ProductStatus findStatusById(@Param("id") Long id);
}
