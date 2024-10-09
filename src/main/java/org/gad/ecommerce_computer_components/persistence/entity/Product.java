package org.gad.ecommerce_computer_components.persistence.entity;

import jakarta.persistence.*;
import lombok.*;
import org.gad.ecommerce_computer_components.persistence.enums.ProductStatus;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "productos")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nombre")
    private String name;

    @Column(name = "descripcion")
    private String description;

    @Column(name = "precio")
    private BigDecimal price;

    @Column(name = "stock")
    private Integer stock;

    @ManyToOne
    @JoinColumn(name = "marca_id")
    private Brand brand;

    @ManyToOne
    @JoinColumn(name = "categoria_id")
    private Category category;

    @CreationTimestamp
    @Column(name = "fecha_creacion", updatable = false)
    private LocalDateTime creationDate;

    @UpdateTimestamp
    @Column(name = "fecha_actualizacion")
    private LocalDateTime updateDate;

    @ManyToOne
    @JoinColumn(name = "modelo_id")
    private Model model;

    @Column(name = "estado")
    @Enumerated(EnumType.STRING)
    private ProductStatus status;

    @Column(name = "imagen")
    private String image;

    @Column(name = "garantia")
    private String warranty;

    @Column(name = "especificaciones")
    private String specs;

    @Column(name = "compatibilidad")
    private String compatibility;

    @Column(name = "fecha_expiracion")
    private LocalDate expirationDate;

    @Column(name = "prescripcion_medica")
    private Boolean prescriptionMedicine;

    @Column(name = "contraindicaciones")
    private String contraindications;

    @Column(name = "lote")
    private String batch;

    @Column(name = "ingredientes")
    private String ingredients;
}
