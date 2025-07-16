package com.asesoria.contable.app_ac.model.dto;

import com.asesoria.contable.app_ac.utils.enums.EstadoPago;
import com.asesoria.contable.app_ac.utils.enums.MedioPago;
import com.asesoria.contable.app_ac.utils.enums.PagadoPor;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class PagoContadorRequest {
    @NotNull
    private LocalDate fechaPago;

    private MedioPago medioPago;

    private String urlVoucher;

    private String comentarioContador;
}
