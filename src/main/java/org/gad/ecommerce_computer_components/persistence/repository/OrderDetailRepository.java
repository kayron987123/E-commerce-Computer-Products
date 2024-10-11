package org.gad.ecommerce_computer_components.persistence.repository;

import org.gad.ecommerce_computer_components.persistence.entity.OrderDetail;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.math.BigDecimal;

public interface OrderDetailRepository extends CrudRepository<OrderDetail, Integer> {

    @Query("SELECT od FROM OrderDetail od WHERE od.order.id = ?1")
    Iterable<OrderDetail> findByOrderId(Long orderId);


}
