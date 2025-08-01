package com.asesoria.contable.app_ac.model.dto;

import com.asesoria.contable.app_ac.utils.enums.DeclaracionEstado;
import com.asesoria.contable.app_ac.utils.enums.EstadoCliente;
import com.asesoria.contable.app_ac.utils.enums.EstadoContador;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class DeclaracionResponse {

    private Long id;
    private ClienteResponse cliente;
    private LocalDate periodoTributario;
    private String tipo;
    private EstadoCliente estadoCliente;
    private EstadoContador estadoContador;
    private LocalDate fechaLimite;
    private String urlConstanciaDeclaracion;
    private String urlConstanciaSunat;
    private BigDecimal totalIngresos;
    private BigDecimal totalEgresos;
    private BigDecimal utilidadEstimada;
    private BigDecimal igvVentas;
    private BigDecimal igvCompras;
    private BigDecimal irEstimado;
    private BigDecimal totalPagarDeclaracion;
    private DeclaracionEstado estado;
}
