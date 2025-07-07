package com.asesoria.contable.app_ac.model.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "contador")
public class Contador {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_contador")
    private Long id;

    @Column(nullable = false, length = 50)
    private String nombres;

    @Column(nullable = false, length = 50)
    private String apellidos;

    @Column(nullable = false, length = 8)
    private String dni;

    @Column(nullable = false, length = 9)
    private String telefono;

    @Column(nullable = true, length = 100)
    private String email;

    @Column(nullable = true, length = 100)
    private String especialidad;

    @Column(name = "nro_colegiatura" , nullable = true, length = 50)
    private String nroColegiatura;

    @OneToOne
    @JoinColumn(name = "id_usuario", nullable = true, unique = true)
    private Usuario usuario;
}
