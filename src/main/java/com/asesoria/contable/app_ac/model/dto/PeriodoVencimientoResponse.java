package com.asesoria.contable.app_ac.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.YearMonth;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PeriodoVencimientoResponse {
    private YearMonth periodo;
    private LocalDate fechaVencimiento;
}