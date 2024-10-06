package org.gad.ecommerce_computer_components.persistence.repository;

import org.gad.ecommerce_computer_components.persistence.entity.ShoppingCart;
import org.springframework.data.repository.CrudRepository;

public interface ShoppingCartRepository extends CrudRepository<ShoppingCart, Long> {
}