package org.gad.ecommerce_computer_components.persistence.repository;

import org.gad.ecommerce_computer_components.persistence.entity.Order;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface OrderRepository extends CrudRepository<Order, Integer> {
    @Query("SELECT o FROM Order o WHERE o.id = ?1")
    Order findOrderById(Long id);

    @Query("SELECT o FROM Order o WHERE o.id = ?1 AND o.user.id = ?2")
    Order findByIdAndUserId(Long idOrder, Long idUser);
}
