package org.gad.ecommerce_computer_components.persistence.entity;

import jakarta.persistence.*;
import lombok.*;
import org.gad.ecommerce_computer_components.persistence.enums.PaymentMethod;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "pagos")
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "pedido_id")
    private Order order;

    @Column(name = "metodo_pago")
    private PaymentMethod paymentMethod;

    @Column(name = "monto")
    private BigDecimal amount;

    @CreationTimestamp
    @Column(name = "fecha_pago", updatable = false)
    private LocalDateTime paymentDate;
}
