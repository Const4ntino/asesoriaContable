package com.asesoria.contable.app_ac.model.entity;

import com.asesoria.contable.app_ac.utils.enums.EstadoPagoNrus;
import com.asesoria.contable.app_ac.utils.enums.MedioPago;
import com.asesoria.contable.app_ac.utils.enums.PagadoPor;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "pago_nrus")
@Data
public class PagoNrus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_pago_nrus")
    private Long id;

    @OneToOne
    @JoinColumn(name = "id_obligacion_nrus", nullable = false)
    private ObligacionNrus obligacionNrus;

    @Column(name = "monto_pagado", nullable = false)
    private BigDecimal montoPagado;

    @Column(name = "fecha_pago", nullable = false)
    private LocalDate fechaPago;

    @Enumerated(EnumType.STRING)
    @Column(name = "medio_pago", nullable = false)
    private MedioPago medioPago;

    @Column(name = "url_comprobante")
    private String urlComprobante;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoPagoNrus estado;

    @Enumerated(EnumType.STRING)
    @Column(name = "pagado_por", nullable = false)
    private PagadoPor pagadoPor;

    @Column(name = "comentario_contador")
    private String comentarioContador;

    @Column(name = "fecha_creacion", updatable = false)
    private LocalDateTime fechaCreacion;

    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;

    @PrePersist
    protected void onCreate() {
        fechaCreacion = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        fechaActualizacion = LocalDateTime.now();
    }
}
