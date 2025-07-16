package com.asesoria.contable.app_ac.model.dto;

import com.asesoria.contable.app_ac.utils.enums.EstadoPagoNrus;
import com.asesoria.contable.app_ac.utils.enums.MedioPago;
import com.asesoria.contable.app_ac.utils.enums.PagadoPor;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class PagoNrusRequest {

    private Long idObligacionNrus;
    private BigDecimal montoPagado;
    private LocalDate fechaPago;
    private MedioPago medioPago;
    private String urlComprobante;
    private EstadoPagoNrus estado;
    private PagadoPor pagadoPor;
    private String comentarioContador;
}
