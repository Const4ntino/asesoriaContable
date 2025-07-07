package com.asesoria.contable.app_ac.model.entity;

import com.asesoria.contable.app_ac.utils.enums.Regimen;
import com.asesoria.contable.app_ac.utils.enums.TipoCliente;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "cliente")
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_cliente")
    private Long id;

    @Column(nullable = false, length = 50)
    private String nombres;

    @Column(nullable = true, length = 50)
    private String apellidos;

    @Column(name = "ruc_dni", nullable = false, length = 11)
    private String rucDni;

    @Column(nullable = true, length = 100)
    private String email;

    @Column(nullable = false, length = 9)
    private String telefono;

    @Column(name = "tipo_ruc", nullable = true, length = 50)
    private String tipoRuc;

    @Enumerated(EnumType.STRING)
    @Column(nullable = true, length = 50)
    private Regimen regimen;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_cliente", nullable = false, length = 50)
    private TipoCliente tipoCliente;

    @OneToOne
    @JoinColumn(name = "id_usuario", nullable = true, unique = true)
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(name = "id_contador", nullable = true)
    private Contador contador;
}
