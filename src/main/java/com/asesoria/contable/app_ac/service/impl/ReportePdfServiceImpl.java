package com.asesoria.contable.app_ac.service.impl;

import com.asesoria.contable.app_ac.dto.reporte.*;
import com.asesoria.contable.app_ac.service.ReportePdfService;
import com.asesoria.contable.app_ac.service.ReporteService;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
// import java.util.stream.Collectors; // No usado

/**
 * Implementación del servicio para generar reportes en formato PDF.
 */
@Service
@RequiredArgsConstructor
public class ReportePdfServiceImpl implements ReportePdfService {

    private final ReporteService reporteService;
    
    private static final Font TITLE_FONT = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD, BaseColor.DARK_GRAY);
    private static final Font SUBTITLE_FONT = new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD, BaseColor.DARK_GRAY);
    private static final Font NORMAL_FONT = new Font(Font.FontFamily.HELVETICA, 10, Font.NORMAL, BaseColor.BLACK);
    private static final Font HEADER_FONT = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD, BaseColor.WHITE);
    private static final Font SMALL_FONT = new Font(Font.FontFamily.HELVETICA, 8, Font.NORMAL, BaseColor.DARK_GRAY);
    
    // Colores para gráficos
    private static final BaseColor[] CHART_COLORS = {
        new BaseColor(41, 128, 185), // Azul
        new BaseColor(39, 174, 96),  // Verde
        new BaseColor(192, 57, 43),  // Rojo
        new BaseColor(142, 68, 173), // Púrpura
        new BaseColor(243, 156, 18), // Naranja
        new BaseColor(44, 62, 80),   // Azul oscuro
        new BaseColor(127, 140, 141) // Gris
    };
    
    // Formato para fechas
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter MONTH_YEAR_FORMATTER = DateTimeFormatter.ofPattern("MMMM yyyy");

    @Override
    public byte[] generarReportePdfCliente(YearMonth periodoTributario, String tipoReporte) {
        // Obtener los datos del reporte usando el servicio existente
        ReporteClienteDTO reporte = reporteService.generarReporteCliente(periodoTributario, tipoReporte);
        
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            Document document = new Document(PageSize.A4);
            // El writer es necesario aunque no se use directamente
            PdfWriter.getInstance(document, baos);
            document.open();
            
            // Agregar encabezado con información del cliente
            agregarEncabezado(document, reporte, periodoTributario);
            
            // Agregar sección de resumen financiero
            if (reporte.getMetricasGenerales() != null) {
                agregarResumenFinanciero(document, reporte);
            }
            
            // Agregar sección de distribución de ingresos
            if (reporte.getIngresos() != null && reporte.getIngresos().getIngresosPorTipoTributario() != null) {
                agregarDistribucionIngresos(document, reporte);
            }
            
            // Agregar sección de distribución de egresos
            if (reporte.getEgresos() != null) {
                agregarDistribucionEgresos(document, reporte);
            }
            
            // Agregar sección de declaraciones
            if (reporte.getDeclaraciones() != null && !reporte.getDeclaraciones().isEmpty()) {
                agregarDeclaraciones(document, reporte);
            }
            
            // Agregar sección de obligaciones
            if (reporte.getObligaciones() != null && !reporte.getObligaciones().isEmpty()) {
                agregarObligaciones(document, reporte);
            }
            
            document.close();
            return baos.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Error al generar el reporte PDF: " + e.getMessage(), e);
        }
    }
    
    /**
     * Agrega el encabezado del reporte con información del cliente y periodo
     */
    private void agregarEncabezado(Document document, ReporteClienteDTO reporte, YearMonth periodoTributario) throws DocumentException {
        // Título principal
        Paragraph titulo = new Paragraph("Reporte de Cliente", TITLE_FONT);
        titulo.setAlignment(Element.ALIGN_CENTER);
        titulo.setSpacingAfter(10);
        document.add(titulo);
        
        // Información del cliente
        if (reporte.getCliente() != null) {
            PdfPTable table = new PdfPTable(2);
            table.setWidthPercentage(100);
            
            // Nombre del cliente
            table.addCell(createCell("Cliente:", true));
            table.addCell(createCell(reporte.getCliente().getNombres() != null ? reporte.getCliente().getNombres() : "", false));
            
            // RUC/DNI
            table.addCell(createCell("RUC/DNI:", true));
            table.addCell(createCell(reporte.getCliente().getRucDni() != null ? reporte.getCliente().getRucDni() : "", false));
            
            // Periodo tributario
            table.addCell(createCell("Periodo:", true));
            table.addCell(createCell(periodoTributario != null ? periodoTributario.format(MONTH_YEAR_FORMATTER) : "", false));
            
            document.add(table);
        }
        
        document.add(Chunk.NEWLINE);
    }
    
    /**
     * Agrega la sección de resumen financiero con métricas generales
     */
    private void agregarResumenFinanciero(Document document, ReporteClienteDTO reporte) throws DocumentException {
        Paragraph titulo = new Paragraph("Resumen Financiero", SUBTITLE_FONT);
        titulo.setSpacingBefore(15);
        titulo.setSpacingAfter(10);
        document.add(titulo);
        
        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);
        
        MetricasGeneralesDTO metricas = reporte.getMetricasGenerales();
        
        table.addCell(createHeaderCell("Métrica"));
        table.addCell(createHeaderCell("Valor"));
        
        table.addCell(createCell("Total Ingresos", false));
        table.addCell(createCell(formatearMonto(metricas.getTotalIngresos()), false));
        
        table.addCell(createCell("Total Egresos", false));
        table.addCell(createCell(formatearMonto(metricas.getTotalEgresos()), false));
        
        table.addCell(createCell("Balance", false));
        table.addCell(createCell(formatearMonto(metricas.getBalance()), false));
        
        document.add(table);
        document.add(Chunk.NEWLINE);
    }
    
    /**
     * Agrega la sección de distribución de ingresos con tabla y gráfico
     */
    private void agregarDistribucionIngresos(Document document, ReporteClienteDTO reporte) throws DocumentException {
        Paragraph titulo = new Paragraph("Distribución de Ingresos", SUBTITLE_FONT);
        titulo.setSpacingBefore(15);
        titulo.setSpacingAfter(10);
        document.add(titulo);
        
        // Tabla de ingresos por tipo tributario
        if (reporte.getIngresos().getIngresosPorTipoTributario() != null) {
            PdfPTable table = new PdfPTable(3);
            table.setWidthPercentage(100);
            
            table.addCell(createHeaderCell("Tipo Tributario"));
            table.addCell(createHeaderCell("Monto"));
            table.addCell(createHeaderCell("Porcentaje"));
            
            BigDecimal totalIngresos = reporte.getMetricasGenerales().getTotalIngresos();
            
            for (Entry<String, BigDecimal> entry : reporte.getIngresos().getIngresosPorTipoTributario().entrySet()) {
                table.addCell(createCell(entry.getKey(), false));
                table.addCell(createCell(formatearMonto(entry.getValue()), false));
                
                // Calcular porcentaje
                BigDecimal porcentaje = BigDecimal.ZERO;
                if (totalIngresos.compareTo(BigDecimal.ZERO) > 0) {
                    porcentaje = entry.getValue().multiply(new BigDecimal(100))
                            .divide(totalIngresos, 2, RoundingMode.HALF_UP);
                }
                table.addCell(createCell(porcentaje + "%", false));
            }
            
            document.add(table);
            document.add(Chunk.NEWLINE);
            
            // Agregar gráfico de distribución de ingresos
            agregarGraficoPastel(document, "Distribución de Ingresos por Tipo Tributario", 
                    reporte.getIngresos().getIngresosPorTipoTributario(), 500, 200);
        }
    }
    
    /**
     * Agrega la sección de distribución de egresos con tabla y gráfico
     */
    private void agregarDistribucionEgresos(Document document, ReporteClienteDTO reporte) throws DocumentException {
        Paragraph titulo = new Paragraph("Distribución de Egresos", SUBTITLE_FONT);
        titulo.setSpacingBefore(15);
        titulo.setSpacingAfter(10);
        document.add(titulo);
        
        // Tabla de egresos por tipo tributario
        if (reporte.getEgresos().getEgresosPorTipoTributario() != null) {
            PdfPTable table = new PdfPTable(3);
            table.setWidthPercentage(100);
            
            table.addCell(createHeaderCell("Tipo Tributario"));
            table.addCell(createHeaderCell("Monto"));
            table.addCell(createHeaderCell("Porcentaje"));
            
            BigDecimal totalEgresos = reporte.getMetricasGenerales().getTotalEgresos();
            
            for (Entry<String, BigDecimal> entry : reporte.getEgresos().getEgresosPorTipoTributario().entrySet()) {
                table.addCell(createCell(entry.getKey(), false));
                table.addCell(createCell(formatearMonto(entry.getValue()), false));
                
                // Calcular porcentaje
                BigDecimal porcentaje = BigDecimal.ZERO;
                if (totalEgresos.compareTo(BigDecimal.ZERO) > 0) {
                    porcentaje = entry.getValue().multiply(new BigDecimal(100))
                            .divide(totalEgresos, 2, RoundingMode.HALF_UP);
                }
                table.addCell(createCell(porcentaje + "%", false));
            }
            
            document.add(table);
            document.add(Chunk.NEWLINE);
            
            // Agregar gráfico de distribución de egresos
            agregarGraficoPastel(document, "Distribución de Egresos por Tipo Tributario", 
                    reporte.getEgresos().getEgresosPorTipoTributario(), 500, 200);
        }
        
        // Tabla de egresos por tipo de contabilidad
        if (reporte.getEgresos().getEgresosPorTipoContabilidad() != null) {
            Paragraph subtitulo = new Paragraph("Egresos por Tipo de Contabilidad", SUBTITLE_FONT);
            subtitulo.setSpacingBefore(15);
            subtitulo.setSpacingAfter(10);
            document.add(subtitulo);
            
            PdfPTable table = new PdfPTable(3);
            table.setWidthPercentage(100);
            
            table.addCell(createHeaderCell("Tipo de Contabilidad"));
            table.addCell(createHeaderCell("Monto"));
            table.addCell(createHeaderCell("Porcentaje"));
            
            BigDecimal totalEgresos = reporte.getMetricasGenerales().getTotalEgresos();
            
            for (Entry<String, BigDecimal> entry : reporte.getEgresos().getEgresosPorTipoContabilidad().entrySet()) {
                table.addCell(createCell(entry.getKey(), false));
                table.addCell(createCell(formatearMonto(entry.getValue()), false));
                
                // Calcular porcentaje
                BigDecimal porcentaje = BigDecimal.ZERO;
                if (totalEgresos.compareTo(BigDecimal.ZERO) > 0) {
                    porcentaje = entry.getValue().multiply(new BigDecimal(100))
                            .divide(totalEgresos, 2, RoundingMode.HALF_UP);
                }
                table.addCell(createCell(porcentaje + "%", false));
            }
            
            document.add(table);
            document.add(Chunk.NEWLINE);
            
            // Agregar gráfico de distribución de egresos por tipo de contabilidad
            agregarGraficoPastel(document, "Distribución de Egresos por Tipo de Contabilidad", 
                    reporte.getEgresos().getEgresosPorTipoContabilidad(), 500, 200);
        }
    }
    
    /**
     * Agrega la sección de declaraciones con tabla
     */
    private void agregarDeclaraciones(Document document, ReporteClienteDTO reporte) throws DocumentException {
        Paragraph titulo = new Paragraph("Historial de Declaraciones", SUBTITLE_FONT);
        titulo.setSpacingBefore(15);
        titulo.setSpacingAfter(10);
        document.add(titulo);
        
        PdfPTable table = new PdfPTable(4);
        table.setWidthPercentage(100);
        
        table.addCell(createHeaderCell("Tipo"));
        table.addCell(createHeaderCell("Fecha"));
        table.addCell(createHeaderCell("Estado"));
        table.addCell(createHeaderCell("Observaciones"));
        
        for (DeclaracionResumenDTO declaracion : reporte.getDeclaraciones()) {
            table.addCell(createCell(declaracion.getTipo() != null ? declaracion.getTipo() : "", false));
            table.addCell(createCell(declaracion.getPeriodoTributario() != null ? declaracion.getPeriodoTributario().toString() : "", false));
            table.addCell(createCell(declaracion.getEstado() != null ? declaracion.getEstado() : "", false));
            table.addCell(createCell("", false));
        }
        
        document.add(table);
        document.add(Chunk.NEWLINE);
    }
    
    /**
     * Agrega la sección de obligaciones con tabla
     */
    private void agregarObligaciones(Document document, ReporteClienteDTO reporte) throws DocumentException {
        Paragraph titulo = new Paragraph("Obligaciones Pendientes", SUBTITLE_FONT);
        titulo.setSpacingBefore(15);
        titulo.setSpacingAfter(10);
        document.add(titulo);
        
        PdfPTable table = new PdfPTable(4);
        table.setWidthPercentage(100);
        
        table.addCell(createHeaderCell("Tipo"));
        table.addCell(createHeaderCell("Fecha Vencimiento"));
        table.addCell(createHeaderCell("Monto"));
        table.addCell(createHeaderCell("Estado"));
        
        for (ObligacionResumenDTO obligacion : reporte.getObligaciones()) {
            table.addCell(createCell(obligacion.getTipo() != null ? obligacion.getTipo() : "", false));
            table.addCell(createCell(obligacion.getFechaLimite() != null ? obligacion.getFechaLimite().format(DATE_FORMATTER) : "", false));
            table.addCell(createCell(formatearMonto(obligacion.getMonto()), false));
            table.addCell(createCell(obligacion.getEstado() != null ? obligacion.getEstado() : "", false));
        }
        
        document.add(table);
        document.add(Chunk.NEWLINE);
    }
    
    /**
     * Crea una celda para una tabla PDF
     */
    private PdfPCell createCell(String text, boolean isHeader) {
        PdfPCell cell = new PdfPCell(new Phrase(text, isHeader ? SUBTITLE_FONT : NORMAL_FONT));
        cell.setPadding(5);
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        return cell;
    }
    
    /**
     * Crea una celda de encabezado para una tabla PDF
     */
    private PdfPCell createHeaderCell(String text) {
        PdfPCell cell = new PdfPCell(new Phrase(text, HEADER_FONT));
        cell.setBackgroundColor(BaseColor.DARK_GRAY);
        cell.setPadding(5);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        return cell;
    }
    
    /**
     * Formatea un monto para mostrar en el reporte
     */
    private String formatearMonto(BigDecimal monto) {
        if (monto == null) {
            return "S/ 0.00";
        }
        return "S/ " + monto.setScale(2, RoundingMode.HALF_UP).toString();
    }
    
    /**
     * Genera un gráfico de pastel simple para visualizar la distribución de datos
     */
    private void agregarGraficoPastel(Document document, String titulo, Map<String, BigDecimal> datos, float width, float height) throws DocumentException {
        // Calcular el total para los porcentajes
        BigDecimal total = datos.values().stream()
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        if (total.compareTo(BigDecimal.ZERO) <= 0) {
            return;
        }
        
        // Agregar título del gráfico
        Paragraph tituloGrafico = new Paragraph(titulo, NORMAL_FONT);
        tituloGrafico.setAlignment(Element.ALIGN_CENTER);
        tituloGrafico.setSpacingBefore(10);
        tituloGrafico.setSpacingAfter(10);
        document.add(tituloGrafico);
        
        // Crear tabla para la leyenda
        PdfPTable leyenda = new PdfPTable(3);
        leyenda.setWidthPercentage(90);
        try {
            leyenda.setWidths(new float[]{1f, 4f, 1f});
        } catch (DocumentException e) {
            // Ignorar error de configuración de anchos
        }
        
        // Agregar encabezados
        leyenda.addCell(createHeaderCell("Color"));
        leyenda.addCell(createHeaderCell("Tipo"));
        leyenda.addCell(createHeaderCell("Porcentaje"));
        
        // Ordenar los datos de mayor a menor para mejor visualización
        List<Entry<String, BigDecimal>> entradasOrdenadas = new ArrayList<>(datos.entrySet());
        entradasOrdenadas.sort((e1, e2) -> e2.getValue().compareTo(e1.getValue()));
        
        // Agregar filas a la leyenda
        int colorIndex = 0;
        for (Entry<String, BigDecimal> entry : entradasOrdenadas) {
            // Celda de color
            PdfPCell colorCell = new PdfPCell();
            colorCell.setBackgroundColor(CHART_COLORS[colorIndex % CHART_COLORS.length]);
            colorCell.setFixedHeight(15f);
            leyenda.addCell(colorCell);
            
            // Celda de tipo
            leyenda.addCell(createCell(entry.getKey(), false));
            
            // Celda de porcentaje
            BigDecimal porcentaje = entry.getValue().multiply(new BigDecimal(100))
                    .divide(total, 2, RoundingMode.HALF_UP);
            leyenda.addCell(createCell(porcentaje + "%", false));
            
            colorIndex++;
        }
        
        document.add(leyenda);
        document.add(Chunk.NEWLINE);
        
        // Llamar al método para crear el gráfico circular
        agregarGraficoCircular(document, datos, total);
    }
    
    /**
     * Crea un gráfico circular usando iText para visualizar proporciones
     */
    private void agregarGraficoCircular(Document document, Map<String, BigDecimal> datos, BigDecimal total) throws DocumentException {
        if (total.compareTo(BigDecimal.ZERO) <= 0) {
            return;
        }
        
        // Como no podemos acceder directamente a PdfContentByte para crear gráficos complejos,
        // usaremos un enfoque alternativo con tablas que simula un gráfico circular
        
        // Crear una tabla para el gráfico circular
        PdfPTable graficoCircular = new PdfPTable(1);
        graficoCircular.setWidthPercentage(60); // Ancho del gráfico respecto a la página
        
        // Crear una celda para contener el "gráfico"
        PdfPCell celdaGrafico = new PdfPCell();
        celdaGrafico.setBorder(Rectangle.NO_BORDER);
        celdaGrafico.setPadding(10f);
        
        // Crear una tabla interna para representar los segmentos del gráfico
        PdfPTable segmentos = new PdfPTable(2); // 2 columnas para crear un efecto circular
        segmentos.setWidthPercentage(100);
        try {
            segmentos.setWidths(new float[]{1f, 1f});
        } catch (DocumentException e) {
            // Ignorar error de configuración de anchos
        }
        
        // Ordenar los datos de mayor a menor para mejor visualización
        List<Entry<String, BigDecimal>> entradasOrdenadas = new ArrayList<>(datos.entrySet());
        entradasOrdenadas.sort((e1, e2) -> e2.getValue().compareTo(e1.getValue()));
        
        // Inicializar el contador de colores
        int colorIndex = 0;
        
        // Crear celdas para representar segmentos del gráfico circular
        for (Entry<String, BigDecimal> entry : entradasOrdenadas) {
            // Calcular el porcentaje proporcional para este valor
            float porcentaje = entry.getValue().divide(total, 4, RoundingMode.HALF_UP).floatValue();
            
            // Crear una celda con el color correspondiente
            PdfPCell segmento = new PdfPCell();
            segmento.setBackgroundColor(CHART_COLORS[colorIndex % CHART_COLORS.length]);
            segmento.setBorder(Rectangle.NO_BORDER);
            
            // La altura es proporcional al porcentaje (para simular el área)
            float altura = Math.max(20f, porcentaje * 200f); // Mínimo 20 puntos para visibilidad
            segmento.setFixedHeight(altura);
            
            // Añadir el segmento a la tabla
            segmentos.addCell(segmento);
            
            // Para la segunda columna, añadir una celda vacía con el mismo color
            // Esto ayuda a crear un efecto más circular
            PdfPCell segmento2 = new PdfPCell();
            segmento2.setBackgroundColor(CHART_COLORS[colorIndex % CHART_COLORS.length]);
            segmento2.setBorder(Rectangle.NO_BORDER);
            segmento2.setFixedHeight(altura);
            segmentos.addCell(segmento2);
            
            // Actualizar contador de colores
            colorIndex++;
        }
        
        // Si hay un número impar de segmentos, añadir una celda vacía al final
        if (datos.size() % 2 != 0) {
            PdfPCell celdaVacia = new PdfPCell();
            celdaVacia.setBorder(Rectangle.NO_BORDER);
            segmentos.addCell(celdaVacia);
        }
        
        // Añadir la tabla de segmentos a la celda principal
        celdaGrafico.addElement(segmentos);
        graficoCircular.addCell(celdaGrafico);
        
        // Añadir el gráfico al documento
        document.add(graficoCircular);
        document.add(Chunk.NEWLINE);
        
        // Añadir una visualización alternativa con barras horizontales para mayor claridad
        crearGraficoAlternativo(document, datos, total);
    }
    
    /**
     * Método alternativo para crear una visualización de datos usando barras horizontales
     * que representan las proporciones de los datos
     */
    private void crearGraficoAlternativo(Document document, Map<String, BigDecimal> datos, BigDecimal total) throws DocumentException {
        // Título para la visualización alternativa
        Paragraph tituloBarras = new Paragraph("Visualización con barras horizontales", NORMAL_FONT);
        tituloBarras.setAlignment(Element.ALIGN_CENTER);
        tituloBarras.setSpacingBefore(5);
        tituloBarras.setSpacingAfter(5);
        document.add(tituloBarras);
        
        // Ordenar los datos de mayor a menor para mejor visualización
        List<Entry<String, BigDecimal>> entradasOrdenadas = new ArrayList<>(datos.entrySet());
        entradasOrdenadas.sort((e1, e2) -> e2.getValue().compareTo(e1.getValue()));
        
        // Crear una tabla con dos columnas: leyendas a la izquierda y barras a la derecha
        PdfPTable tablaPrincipal = new PdfPTable(2);
        tablaPrincipal.setWidthPercentage(90);
        
        try {
            // La columna de leyendas ocupa 20% del ancho, las barras 80%
            tablaPrincipal.setWidths(new float[]{0.2f, 0.8f});
        } catch (DocumentException e) {
            // Ignorar si no se pueden establecer los anchos
        }
        
        // Crear una tabla para las leyendas (columna izquierda)
        PdfPTable tablaLeyendas = new PdfPTable(1);
        
        // Crear una tabla para las barras (columna derecha)
        PdfPTable tablaBarras = new PdfPTable(1);
        
        // Procesar cada entrada de datos
        for (int i = 0; i < entradasOrdenadas.size(); i++) {
            Entry<String, BigDecimal> entry = entradasOrdenadas.get(i);
            
            // Calcular el porcentaje para esta entrada
            float proporcion = entry.getValue().divide(total, 4, RoundingMode.HALF_UP).floatValue();
            int porcentajeEntero = Math.round(proporcion * 100);
            
            // Celda para la leyenda (izquierda)
            PdfPCell celdaLeyenda = new PdfPCell(new Phrase(entry.getKey() + ": " + porcentajeEntero + "%", SMALL_FONT));
            celdaLeyenda.setBorder(Rectangle.NO_BORDER);
            celdaLeyenda.setPadding(5f);
            celdaLeyenda.setHorizontalAlignment(Element.ALIGN_RIGHT);
            celdaLeyenda.setVerticalAlignment(Element.ALIGN_MIDDLE);
            tablaLeyendas.addCell(celdaLeyenda);
            
            // Crear una tabla para esta barra horizontal
            PdfPTable barraSola = new PdfPTable(2);
            try {
                // La primera columna es la barra coloreada, la segunda es el espacio restante
                barraSola.setWidths(new float[]{proporcion, 1 - proporcion});
            } catch (DocumentException e) {
                // Si hay error en los anchos, usar valores por defecto
                try {
                    barraSola.setWidths(new float[]{0.5f, 0.5f});
                } catch (DocumentException ex) {
                    // Ignorar si no se pueden establecer los anchos
                }
            }
            
            // Celda para la barra coloreada
            PdfPCell barraCelda = new PdfPCell();
            barraCelda.setBackgroundColor(CHART_COLORS[i % CHART_COLORS.length]);
            barraCelda.setBorder(Rectangle.BOX);
            barraCelda.setFixedHeight(20f);
            barraSola.addCell(barraCelda);
            
            // Celda para el espacio restante (transparente)
            PdfPCell espacioCelda = new PdfPCell();
            espacioCelda.setBorder(Rectangle.NO_BORDER);
            barraSola.addCell(espacioCelda);
            
            // Añadir esta barra a la tabla de barras
            PdfPCell contenedorBarra = new PdfPCell();
            contenedorBarra.addElement(barraSola);
            contenedorBarra.setBorder(Rectangle.NO_BORDER);
            contenedorBarra.setPadding(5f);
            tablaBarras.addCell(contenedorBarra);
        }
        
        // Añadir las tablas de leyendas y barras a la tabla principal
        PdfPCell celdaLeyendas = new PdfPCell();
        celdaLeyendas.addElement(tablaLeyendas);
        celdaLeyendas.setBorder(Rectangle.NO_BORDER);
        tablaPrincipal.addCell(celdaLeyendas);
        
        PdfPCell celdaBarras = new PdfPCell();
        celdaBarras.addElement(tablaBarras);
        celdaBarras.setBorder(Rectangle.NO_BORDER);
        tablaPrincipal.addCell(celdaBarras);
        
        // Añadir la tabla principal al documento
        document.add(tablaPrincipal);
        document.add(Chunk.NEWLINE);
    }
}