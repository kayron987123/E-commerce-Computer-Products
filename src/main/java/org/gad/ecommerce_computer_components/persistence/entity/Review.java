package org.gad.ecommerce_computer_components.persistence.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "resenias")
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "producto_id")
    private Product product;

    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private UserEntity user;

    @Column(name = "calificacion")
    private Integer qualification;

    @Column(name = "comentario")
    private String comment;

    @CreationTimestamp
    @Column(name = "fecha_creacion", updatable = false)
    private LocalDateTime cretionDate;

    @UpdateTimestamp
    @Column(name = "fecha_actualizacion")
    private LocalDateTime updateDate;
}
