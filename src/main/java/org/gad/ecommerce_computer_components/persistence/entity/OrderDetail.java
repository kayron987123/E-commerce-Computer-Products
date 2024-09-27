package org.gad.ecommerce_computer_components.persistence.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Getter
@Setter
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "detalles_pedidos")
public class OrderDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "pedido_id")
    private Order order;

    @ManyToOne
    @JoinColumn(name = "producto_id")
    private Product product;

    @Column(name = "cantidad")
    private Integer amount;

    @Column(name = "precio_unitario")
    private BigDecimal unitPrice;
}
