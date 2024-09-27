package org.gad.ecommerce_computer_components.persistence.entity;

import jakarta.persistence.*;
import lombok.*;
import org.gad.ecommerce_computer_components.persistence.enums.AccountStatement;
import org.gad.ecommerce_computer_components.persistence.enums.Role;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

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
@Table(name = "usuarios")
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nombre")
    private String name;

    @Column(name = "apellido")
    private String lastName;

    @Column(name = "nombre_usuario", unique = true, nullable = false)
    private String username;

    @Column(name = "correo_electronico", unique = true)
    private String email;

    @Column(name = "contrasenia", nullable = false)
    private String password;

    @Column(name = "direccion")
    private String address;

    @Column(name = "celular")
    private String cellphone;

    @Column(name = "rol")
    @Enumerated(EnumType.STRING)
    private Role role;

    @Column(name = "imagen_perfil")
    private String profileImage;

    @Column(name = "dni")
    private String dni;

    @CreationTimestamp
    @Column(name = "fecha_creacion", updatable = false)
    private LocalDateTime creationDate;

    @UpdateTimestamp
    @Column(name = "fecha_actualizacion")
    private LocalDateTime updateDate;

    @Column(name = "estado_cuenta")
    @Enumerated(EnumType.STRING)
    private AccountStatement accountStatus;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<Order> orders = new ArrayList<>();

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<Review> reviews = new ArrayList<>();

}
