package com.asesoria.contable.app_ac.model.entity;

import com.asesoria.contable.app_ac.utils.enums.TipoContabilidad;
import com.asesoria.contable.app_ac.utils.enums.TipoTributario;
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
@Table(name = "egreso")
public class Egreso {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_egreso")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "id_cliente", nullable = false)
    private Cliente cliente;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal monto;

    @Column(name = "monto_igv", nullable = false, precision = 12, scale = 2)
    private BigDecimal montoIgv;

    @Column(nullable = false)
    private LocalDate fecha;

    @Column(columnDefinition = "TEXT")
    private String descripcion;

    @Column(name = "nro_comprobante", length = 50)
    private String nroComprobante;

    @Column(name = "url_comprobante", length = 255)
    private String urlComprobante;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_contabilidad", length = 20)
    private TipoContabilidad tipoContabilidad;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_tributario", length = 20)
    private TipoTributario tipoTributario;

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
