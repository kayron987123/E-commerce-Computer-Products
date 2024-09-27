package org.gad.ecommerce_computer_components.persistence.entity;

import jakarta.persistence.*;
import lombok.*;
import org.gad.ecommerce_computer_components.persistence.enums.OrderStatus;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "pedidos")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private UserEntity user;

    @CreationTimestamp
    @Column(name = "fecha_pedido", updatable = false)
    private LocalDateTime orderDate;

    @Column(name = "fecha_entrega")
    private LocalDateTime deliveryDate;

    @Column(name = "estado")
    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    @Column(name = "total_a_pagar")
    private BigDecimal totalToPay;

    @Column(name = "direccion_envio")
    private String shippingAddress;

    @OneToMany(mappedBy = "order", fetch = FetchType.LAZY)
    private List<OrderDetail> orderDetails = new ArrayList<>();

    @OneToMany(mappedBy = "order", fetch = FetchType.LAZY)
    private List<Payment> payments = new ArrayList<>();
}
