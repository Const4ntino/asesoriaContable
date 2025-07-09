package com.asesoria.contable.app_ac.model.entity;

import com.asesoria.contable.app_ac.utils.enums.EstadoObligacion;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "obligacion")
@Data
@NoArgsConstructor
public class Obligacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_obligacion")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "id_declaracion")
    private Declaracion declaracion;

    @ManyToOne
    @JoinColumn(name = "id_cliente", nullable = false)
    private Cliente cliente;

    @Column(nullable = false, length = 100)
    private String tipo;

    @Column(name = "periodo_tributario", nullable = false)
    private LocalDate periodoTributario;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal monto;

    @Column(name = "fecha_limite")
    private LocalDate fechaLimite;

    @Enumerated(EnumType.STRING)
    @Column(length = 30)
    private EstadoObligacion estado;

    @Column
    private String observaciones;

    @Column(name = "fecha_creacion", updatable = false)
    private LocalDateTime fechaCreacion;

    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;

    @PrePersist
    protected void onCreate() {
        fechaCreacion = LocalDateTime.now();
        fechaActualizacion = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        fechaActualizacion = LocalDateTime.now();
    }
}
