package com.asesoria.contable.app_ac.service;

import com.asesoria.contable.app_ac.dto.reporte.ReporteClienteDTO;
import java.time.YearMonth;

/**
 * Servicio para la generación de reportes con métricas de clientes.
 */
public interface ReporteService {
    
    /**
     * Genera un reporte con métricas para el cliente actual basado en el periodo y tipo de reporte solicitado.
     * 
     * @param periodoTributario Periodo tributario para el cual generar el reporte
     * @param tipoReporte Tipo de reporte a generar (COMPLETO, INGRESOS, EGRESOS, DECLARACIONES, OBLIGACIONES)
     * @return ReporteClienteDTO con la información solicitada
     */
    ReporteClienteDTO generarReporteCliente(YearMonth periodoTributario, String tipoReporte);
}
