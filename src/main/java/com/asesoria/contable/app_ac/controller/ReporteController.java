package com.asesoria.contable.app_ac.controller;

import com.asesoria.contable.app_ac.dto.reporte.ReporteClienteDTO;
import com.asesoria.contable.app_ac.service.ReporteService;
import com.asesoria.contable.app_ac.service.ReportePdfService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.YearMonth;

@RestController
@RequestMapping("/api/v1/reportes")
public class ReporteController {

    private final ReporteService reporteService;
    private final ReportePdfService reportePdfService;

    public ReporteController(ReporteService reporteService, ReportePdfService reportePdfService) {
        this.reporteService = reporteService;
        this.reportePdfService = reportePdfService;
    }

    /**
     * Endpoint para obtener un reporte consolidado de métricas del cliente.
     * 
     * @param anio Año del reporte (opcional si se proporciona periodo)
     * @param mes Mes del reporte (opcional si se proporciona periodo)
     * @param periodo Periodo en formato "YYYY-MM" (opcional si se proporcionan año y mes)
     * @param tipoReporte Tipo de reporte: COMPLETO, INGRESOS, EGRESOS, DECLARACIONES, OBLIGACIONES
     * @return ReporteClienteDTO con la información solicitada
     */
    @GetMapping("/cliente/metricas")
    @PreAuthorize("hasRole('CLIENTE')")
    public ResponseEntity<ReporteClienteDTO> getReporteCliente(
            @RequestParam(required = false) Integer anio,
            @RequestParam(required = false) Integer mes,
            @RequestParam(required = false) String periodo,
            @RequestParam(required = false, defaultValue = "COMPLETO") String tipoReporte) {
        
        // Validar y procesar los parámetros
        YearMonth periodoTributario;
        if (periodo != null && !periodo.isEmpty()) {
            // Parsear periodo en formato "YYYY-MM"
            String[] parts = periodo.split("-");
            periodoTributario = YearMonth.of(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]));
        } else if (anio != null) {
            // Usar año y mes si están disponibles
            periodoTributario = YearMonth.of(anio, mes != null ? mes : 1);
        } else {
            // Usar mes actual por defecto
            periodoTributario = YearMonth.now();
        }
        
        // Obtener el reporte
        ReporteClienteDTO reporte = reporteService.generarReporteCliente(periodoTributario, tipoReporte);
        
        return ResponseEntity.ok(reporte);
    }
    
    /**
     * Endpoint para descargar un reporte consolidado de métricas del cliente en formato PDF.
     * 
     * @param anio Año del reporte (opcional si se proporciona periodo)
     * @param mes Mes del reporte (opcional si se proporciona periodo)
     * @param periodo Periodo en formato "YYYY-MM" (opcional si se proporcionan año y mes)
     * @param tipoReporte Tipo de reporte: COMPLETO, INGRESOS, EGRESOS, DECLARACIONES, OBLIGACIONES
     * @return Archivo PDF con el reporte solicitado
     */
    @GetMapping(value = "/cliente/metricas/pdf", produces = MediaType.APPLICATION_PDF_VALUE)
    @PreAuthorize("hasRole('CLIENTE')")
    public ResponseEntity<byte[]> getReporteClientePdf(
            @RequestParam(required = false) Integer anio,
            @RequestParam(required = false) Integer mes,
            @RequestParam(required = false) String periodo,
            @RequestParam(required = false, defaultValue = "COMPLETO") String tipoReporte) {
        
        // Validar y procesar los parámetros
        YearMonth periodoTributario;
        if (periodo != null && !periodo.isEmpty()) {
            // Parsear periodo en formato "YYYY-MM"
            String[] parts = periodo.split("-");
            periodoTributario = YearMonth.of(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]));
        } else if (anio != null) {
            // Usar año y mes si están disponibles
            periodoTributario = YearMonth.of(anio, mes != null ? mes : 1);
        } else {
            // Usar mes actual por defecto
            periodoTributario = YearMonth.now();
        }
        
        // Generar el reporte en formato PDF
        byte[] reportePdf = reportePdfService.generarReportePdfCliente(periodoTributario, tipoReporte);
        
        // Configurar headers para la descarga
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "reporte-cliente-" + periodoTributario + ".pdf");
        headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");
        
        return ResponseEntity
                .ok()
                .headers(headers)
                .body(reportePdf);
    }
}
