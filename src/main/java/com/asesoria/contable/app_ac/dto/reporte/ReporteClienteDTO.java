package com.asesoria.contable.app_ac.dto.reporte;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReporteClienteDTO {
    // Información del cliente
    private ClienteInfoDTO cliente;
    
    // Métricas generales
    private MetricasGeneralesDTO metricasGenerales;
    
    // Métricas de ingresos
    private MetricasIngresosDTO ingresos;
    
    // Métricas de egresos
    private MetricasEgresosDTO egresos;
    
    // Información de declaraciones
    private List<DeclaracionResumenDTO> declaraciones;
    
    // Información de obligaciones
    private List<ObligacionResumenDTO> obligaciones;
    
    // Información de pagos
    private List<PagoResumenDTO> pagos;
}
