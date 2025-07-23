package com.asesoria.contable.app_ac.service;

import java.time.YearMonth;

/**
 * Servicio para la generación de reportes en formato PDF.
 */
public interface ReportePdfService {
    
    /**
     * Genera un reporte PDF con métricas para el cliente actual basado en el periodo y tipo de reporte solicitado.
     * 
     * @param periodoTributario Periodo tributario para el cual generar el reporte
     * @param tipoReporte Tipo de reporte a generar (COMPLETO, INGRESOS, EGRESOS, DECLARACIONES, OBLIGACIONES)
     * @return Array de bytes con el contenido del PDF generado
     */
    byte[] generarReportePdfCliente(YearMonth periodoTributario, String tipoReporte);
}
