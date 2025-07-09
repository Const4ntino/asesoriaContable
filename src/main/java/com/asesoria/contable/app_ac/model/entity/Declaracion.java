package com.asesoria.contable.app_ac.model.entity;

import com.asesoria.contable.app_ac.utils.enums.DeclaracionEstado;
import com.asesoria.contable.app_ac.utils.enums.EstadoCliente;
import com.asesoria.contable.app_ac.utils.enums.EstadoContador;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "declaracion")
public class Declaracion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_declaracion")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "id_cliente", nullable = false)
    private Cliente cliente;

    @Column(name = "periodo_tributario", nullable = false)
    private LocalDate periodoTributario;

    @Column(nullable = false)
    private String tipo;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado_cliente")
    private EstadoCliente estadoCliente;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado_contador")
    private EstadoContador estadoContador;

    @Column(name = "fecha_limite")
    private LocalDate fechaLimite;

    @Column(name = "url_constancia_declaracion")
    private String urlConstanciaDeclaracion;

    @Column(name = "url_constancia_pago")
    private String urlConstanciaPago;

    @Column(name = "total_ingresos")
    private BigDecimal totalIngresos;

    @Column(name = "total_egresos")
    private BigDecimal totalEgresos;

    @Column(name = "utilidad_estimada")
    private BigDecimal utilidadEstimada;

    @Column(name = "igv_ventas")
    private BigDecimal igvVentas;

    @Column(name = "igv_compras")
    private BigDecimal igvCompras;

    @Column(name = "ir_estimado")
    private BigDecimal irEstimado;

    @Column(name = "total_pagar_declaracion")
    private BigDecimal totalPagarDeclaracion;

    @Column(name = "fecha_creacion", updatable = false)
    private LocalDateTime fechaCreacion;

    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado")
    private DeclaracionEstado estado;

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
