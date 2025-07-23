package com.asesoria.contable.app_ac.service.impl;

import com.asesoria.contable.app_ac.dto.reporte.*;
import com.asesoria.contable.app_ac.model.entity.*;
import com.asesoria.contable.app_ac.repository.*;
import com.asesoria.contable.app_ac.service.ReporteService;
import com.asesoria.contable.app_ac.utils.enums.TipoContabilidad;
import com.asesoria.contable.app_ac.utils.enums.TipoTributario;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ReporteServiceImpl implements ReporteService {

    private final ClienteRepository clienteRepository;
    private final IngresoRepository ingresoRepository;
    private final EgresoRepository egresoRepository;
    private final DeclaracionRepository declaracionRepository;
    private final ObligacionRepository obligacionRepository;
    private final PagoRepository pagoRepository;
    private final UsuarioRepository usuarioRepository;

    public ReporteServiceImpl(
            ClienteRepository clienteRepository,
            IngresoRepository ingresoRepository,
            EgresoRepository egresoRepository,
            DeclaracionRepository declaracionRepository,
            ObligacionRepository obligacionRepository,
            PagoRepository pagoRepository,
            UsuarioRepository usuarioRepository) {
        this.clienteRepository = clienteRepository;
        this.ingresoRepository = ingresoRepository;
        this.egresoRepository = egresoRepository;
        this.declaracionRepository = declaracionRepository;
        this.obligacionRepository = obligacionRepository;
        this.pagoRepository = pagoRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public ReporteClienteDTO generarReporteCliente(YearMonth periodoTributario, String tipoReporte) {
        // Obtener el cliente actual
        Cliente cliente = obtenerClienteActual();
        
        ReporteClienteDTO reporte = new ReporteClienteDTO();
        
        // Información básica del cliente
        reporte.setCliente(mapClienteInfo(cliente));
        
        // Fechas para filtrado
        LocalDate inicioMes = periodoTributario.atDay(1);
        LocalDate finMes = periodoTributario.atEndOfMonth();
        
        // Generar las métricas según el tipo de reporte solicitado
        if (tipoReporte.equals("COMPLETO") || tipoReporte.equals("INGRESOS")) {
            reporte.setIngresos(generarMetricasIngresos(cliente, inicioMes, finMes));
        }
        
        if (tipoReporte.equals("COMPLETO") || tipoReporte.equals("EGRESOS")) {
            reporte.setEgresos(generarMetricasEgresos(cliente, inicioMes, finMes));
        }
        
        if (tipoReporte.equals("COMPLETO")) {
            reporte.setMetricasGenerales(generarMetricasGenerales(cliente, inicioMes, finMes));
        }
        
        if (tipoReporte.equals("COMPLETO") || tipoReporte.equals("DECLARACIONES")) {
            reporte.setDeclaraciones(generarResumenDeclaraciones(cliente, periodoTributario));
        }
        
        if (tipoReporte.equals("COMPLETO") || tipoReporte.equals("OBLIGACIONES")) {
            reporte.setObligaciones(generarResumenObligaciones(cliente, periodoTributario));
            reporte.setPagos(generarResumenPagos(cliente, periodoTributario));
        }
        
        return reporte;
    }
    
    /**
     * Obtiene el cliente actual basado en el usuario autenticado
     */
    private Cliente obtenerClienteActual() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        
        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        return clienteRepository.findByUsuarioId(usuario.getId())
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));
    }
    
    /**
     * Mapea la información básica del cliente
     */
    private ClienteInfoDTO mapClienteInfo(Cliente cliente) {
        return ClienteInfoDTO.builder()
                .id(cliente.getId())
                .nombres(cliente.getNombres())
                .apellidos(cliente.getApellidos())
                .rucDni(cliente.getRucDni())
                .tipoRuc(cliente.getTipoRuc())
                .regimen(cliente.getRegimen() != null ? cliente.getRegimen().toString() : null)
                .tipoCliente(cliente.getTipoCliente() != null ? cliente.getTipoCliente().toString() : null)
                .email(cliente.getEmail())
                .telefono(cliente.getTelefono())
                .build();
    }
    
    /**
     * Genera métricas generales del cliente
     */
    private MetricasGeneralesDTO generarMetricasGenerales(Cliente cliente, LocalDate inicioMes, LocalDate finMes) {
        // Obtener totales de ingresos y egresos
        BigDecimal totalIngresos = obtenerTotalIngresos(cliente.getId(), inicioMes, finMes);
        BigDecimal totalEgresos = obtenerTotalEgresos(cliente.getId(), inicioMes, finMes);
        
        // Calcular balance
        BigDecimal balance = totalIngresos.subtract(totalEgresos);
        
        // Calcular utilidad estimada (simplificado)
        BigDecimal utilidadEstimada = balance;
        
        // Calcular IGV por pagar (ingresos - egresos)
        BigDecimal igvIngresos = obtenerTotalIgvIngresos(cliente.getId(), inicioMes, finMes);
        BigDecimal igvEgresos = obtenerTotalIgvEgresos(cliente.getId(), inicioMes, finMes);
        BigDecimal igvPorPagar = igvIngresos.subtract(igvEgresos);
        
        // Calcular IR estimado (simplificado, 1.5% de los ingresos netos)
        BigDecimal irEstimado = totalIngresos.multiply(new BigDecimal("0.015")).setScale(2, RoundingMode.HALF_UP);
        
        // Generar tendencia mensual (últimos 6 meses)
        Map<String, BigDecimal> tendenciaMensual = generarTendenciaMensual(cliente.getId(), inicioMes);
        
        return MetricasGeneralesDTO.builder()
                .totalIngresos(totalIngresos)
                .totalEgresos(totalEgresos)
                .balance(balance)
                .utilidadEstimada(utilidadEstimada)
                .igvPorPagar(igvPorPagar)
                .irEstimado(irEstimado)
                .tendenciaMensual(tendenciaMensual)
                .build();
    }
    
    /**
     * Genera métricas de ingresos
     */
    private MetricasIngresosDTO generarMetricasIngresos(Cliente cliente, LocalDate inicioMes, LocalDate finMes) {
        // Calcular total del mes actual
        BigDecimal totalMesActual = obtenerTotalIngresos(cliente.getId(), inicioMes, finMes);
        
        // Calcular total del mes anterior
        LocalDate inicioMesAnterior = inicioMes.minusMonths(1);
        LocalDate finMesAnterior = inicioMesAnterior.plusMonths(1).minusDays(1);
        BigDecimal totalMesAnterior = obtenerTotalIngresos(cliente.getId(), inicioMesAnterior, finMesAnterior);
        
        // Calcular variación porcentual
        BigDecimal variacionPorcentual = calcularVariacionPorcentual(totalMesActual, totalMesAnterior);
        
        // Obtener distribución por tipo tributario
        Map<String, BigDecimal> ingresosPorTipoTributario = obtenerIngresosPorTipoTributario(cliente.getId(), inicioMes, finMes);
        
        // Obtener últimos ingresos
        List<IngresoResumenDTO> ultimosIngresos = obtenerUltimosIngresos(cliente.getId());
        
        return MetricasIngresosDTO.builder()
                .totalMesActual(totalMesActual)
                .totalMesAnterior(totalMesAnterior)
                .variacionPorcentual(variacionPorcentual)
                .ingresosPorTipoTributario(ingresosPorTipoTributario)
                .ultimosIngresos(ultimosIngresos)
                .build();
    }
    
    /**
     * Genera métricas de egresos
     */
    private MetricasEgresosDTO generarMetricasEgresos(Cliente cliente, LocalDate inicioMes, LocalDate finMes) {
        // Calcular total del mes actual
        BigDecimal totalMesActual = obtenerTotalEgresos(cliente.getId(), inicioMes, finMes);
        
        // Calcular total del mes anterior
        LocalDate inicioMesAnterior = inicioMes.minusMonths(1);
        LocalDate finMesAnterior = inicioMesAnterior.plusMonths(1).minusDays(1);
        BigDecimal totalMesAnterior = obtenerTotalEgresos(cliente.getId(), inicioMesAnterior, finMesAnterior);
        
        // Calcular variación porcentual
        BigDecimal variacionPorcentual = calcularVariacionPorcentual(totalMesActual, totalMesAnterior);
        
        // Obtener distribución por tipo tributario
        Map<String, BigDecimal> egresosPorTipoTributario = obtenerEgresosPorTipoTributario(cliente.getId(), inicioMes, finMes);
        
        // Obtener distribución por tipo contabilidad
        Map<String, BigDecimal> egresosPorTipoContabilidad = obtenerEgresosPorTipoContabilidad(cliente.getId(), inicioMes, finMes);
        
        // Obtener últimos egresos
        List<EgresoResumenDTO> ultimosEgresos = obtenerUltimosEgresos(cliente.getId());
        
        return MetricasEgresosDTO.builder()
                .totalMesActual(totalMesActual)
                .totalMesAnterior(totalMesAnterior)
                .variacionPorcentual(variacionPorcentual)
                .egresosPorTipoTributario(egresosPorTipoTributario)
                .egresosPorTipoContabilidad(egresosPorTipoContabilidad)
                .ultimosEgresos(ultimosEgresos)
                .build();
    }
    
    /**
     * Genera resumen de declaraciones
     */
    private List<DeclaracionResumenDTO> generarResumenDeclaraciones(Cliente cliente, YearMonth periodoTributario) {
        LocalDate inicioPeriodo = periodoTributario.atDay(1);
        LocalDate finPeriodo = periodoTributario.atEndOfMonth();
        
        List<Declaracion> declaraciones = declaracionRepository.findByClienteIdAndPeriodoTributarioBetween(
                cliente.getId(), inicioPeriodo, finPeriodo);
        
        return declaraciones.stream()
                .map(this::mapDeclaracionResumen)
                .collect(Collectors.toList());
    }
    
    /**
     * Genera resumen de obligaciones
     */
    private List<ObligacionResumenDTO> generarResumenObligaciones(Cliente cliente, YearMonth periodoTributario) {
        LocalDate inicioPeriodo = periodoTributario.atDay(1);
        LocalDate finPeriodo = periodoTributario.atEndOfMonth();
        
        List<Obligacion> obligaciones = obligacionRepository.findByClienteIdAndPeriodoTributarioBetween(
                cliente.getId(), inicioPeriodo, finPeriodo);
        
        return obligaciones.stream()
                .map(this::mapObligacionResumen)
                .collect(Collectors.toList());
    }
    
    /**
     * Genera resumen de pagos
     */
    private List<PagoResumenDTO> generarResumenPagos(Cliente cliente, YearMonth periodoTributario) {
        LocalDate inicioPeriodo = periodoTributario.atDay(1);
        LocalDate finPeriodo = periodoTributario.atEndOfMonth();
        
        List<Pago> pagos = pagoRepository.findByObligacionClienteIdAndObligacionPeriodoTributarioBetween(
                cliente.getId(), inicioPeriodo, finPeriodo);
        
        return pagos.stream()
                .map(this::mapPagoResumen)
                .collect(Collectors.toList());
    }
    
    // Métodos auxiliares para obtener datos
    
    private BigDecimal obtenerTotalIngresos(Long clienteId, LocalDate fechaInicio, LocalDate fechaFin) {
        BigDecimal total = ingresoRepository.sumMontoByClienteIdAndFechaBetween(clienteId, fechaInicio, fechaFin);
        return total != null ? total : BigDecimal.ZERO;
    }
    
    private BigDecimal obtenerTotalEgresos(Long clienteId, LocalDate fechaInicio, LocalDate fechaFin) {
        BigDecimal total = egresoRepository.sumMontoByClienteIdAndFechaBetween(clienteId, fechaInicio, fechaFin);
        return total != null ? total : BigDecimal.ZERO;
    }
    
    private BigDecimal obtenerTotalIgvIngresos(Long clienteId, LocalDate fechaInicio, LocalDate fechaFin) {
        BigDecimal total = ingresoRepository.sumMontoIgvByClienteIdAndFechaBetween(clienteId, fechaInicio, fechaFin);
        return total != null ? total : BigDecimal.ZERO;
    }
    
    private BigDecimal obtenerTotalIgvEgresos(Long clienteId, LocalDate fechaInicio, LocalDate fechaFin) {
        BigDecimal total = egresoRepository.sumMontoIgvByClienteIdAndFechaBetween(clienteId, fechaInicio, fechaFin);
        return total != null ? total : BigDecimal.ZERO;
    }
    
    private BigDecimal calcularVariacionPorcentual(BigDecimal valorActual, BigDecimal valorAnterior) {
        if (valorAnterior.compareTo(BigDecimal.ZERO) > 0) {
            return valorActual.subtract(valorAnterior)
                    .divide(valorAnterior, 4, RoundingMode.HALF_UP)
                    .multiply(new BigDecimal(100))
                    .setScale(2, RoundingMode.HALF_UP);
        }
        return BigDecimal.ZERO;
    }
    
    private Map<String, BigDecimal> generarTendenciaMensual(Long clienteId, LocalDate fechaReferencia) {
        Map<String, BigDecimal> tendencia = new LinkedHashMap<>();
        
        // Generar datos para los últimos 6 meses
        for (int i = 5; i >= 0; i--) {
            YearMonth mes = YearMonth.from(fechaReferencia).minusMonths(i);
            LocalDate inicioMes = mes.atDay(1);
            LocalDate finMes = mes.atEndOfMonth();
            
            BigDecimal ingresos = obtenerTotalIngresos(clienteId, inicioMes, finMes);
            BigDecimal egresos = obtenerTotalEgresos(clienteId, inicioMes, finMes);
            BigDecimal balance = ingresos.subtract(egresos);
            
            tendencia.put(mes.toString(), balance);
        }
        
        return tendencia;
    }
    
    private Map<String, BigDecimal> obtenerIngresosPorTipoTributario(Long clienteId, LocalDate fechaInicio, LocalDate fechaFin) {
        Map<String, BigDecimal> distribucion = new HashMap<>();
        
        // Inicializar con todos los tipos tributarios
        for (TipoTributario tipo : TipoTributario.values()) {
            distribucion.put(tipo.name(), BigDecimal.ZERO);
        }
        
        // Obtener datos reales
        List<Object[]> resultados = ingresoRepository.findDistribucionPorTipoTributario(clienteId, fechaInicio, fechaFin);
        
        for (Object[] resultado : resultados) {
            String tipoTributario = ((TipoTributario) resultado[0]).name();
            BigDecimal monto = (BigDecimal) resultado[1];
            distribucion.put(tipoTributario, monto);
        }
        
        return distribucion;
    }
    
    private Map<String, BigDecimal> obtenerEgresosPorTipoTributario(Long clienteId, LocalDate fechaInicio, LocalDate fechaFin) {
        Map<String, BigDecimal> distribucion = new HashMap<>();
        
        // Inicializar con todos los tipos tributarios
        for (TipoTributario tipo : TipoTributario.values()) {
            distribucion.put(tipo.name(), BigDecimal.ZERO);
        }
        
        // Obtener datos reales
        List<Object[]> resultados = egresoRepository.findDistribucionPorTipoTributario(clienteId, fechaInicio, fechaFin);
        
        for (Object[] resultado : resultados) {
            String tipoTributario = ((TipoTributario) resultado[0]).name();
            BigDecimal monto = (BigDecimal) resultado[1];
            distribucion.put(tipoTributario, monto);
        }
        
        return distribucion;
    }
    
    private Map<String, BigDecimal> obtenerEgresosPorTipoContabilidad(Long clienteId, LocalDate fechaInicio, LocalDate fechaFin) {
        Map<String, BigDecimal> distribucion = new HashMap<>();
        
        // Inicializar con todos los tipos de contabilidad
        for (TipoContabilidad tipo : TipoContabilidad.values()) {
            distribucion.put(tipo.name(), BigDecimal.ZERO);
        }
        
        // Obtener datos reales
        List<Object[]> resultados = egresoRepository.findDistribucionPorTipoContabilidad(clienteId, fechaInicio, fechaFin);
        
        for (Object[] resultado : resultados) {
            String tipoContabilidad = ((TipoContabilidad) resultado[0]).name();
            BigDecimal monto = (BigDecimal) resultado[1];
            distribucion.put(tipoContabilidad, monto);
        }
        
        return distribucion;
    }
    
    private List<IngresoResumenDTO> obtenerUltimosIngresos(Long clienteId) {
        List<Ingreso> ingresos = ingresoRepository.findTop5ByClienteIdOrderByFechaDesc(clienteId);
        return ingresos.stream()
                .map(this::mapIngresoResumen)
                .collect(Collectors.toList());
    }
    
    private List<EgresoResumenDTO> obtenerUltimosEgresos(Long clienteId) {
        List<Egreso> egresos = egresoRepository.findTop5ByClienteIdOrderByFechaDesc(clienteId);
        return egresos.stream()
                .map(this::mapEgresoResumen)
                .collect(Collectors.toList());
    }
    
    // Métodos de mapeo de entidades a DTOs
    
    private IngresoResumenDTO mapIngresoResumen(Ingreso ingreso) {
        return IngresoResumenDTO.builder()
                .id(ingreso.getId())
                .fecha(ingreso.getFecha())
                .monto(ingreso.getMonto())
                .montoIgv(ingreso.getMontoIgv())
                .descripcion(ingreso.getDescripcion())
                .nroComprobante(ingreso.getNroComprobante())
                .tipoTributario(ingreso.getTipoTributario().name())
                .build();
    }
    
    private EgresoResumenDTO mapEgresoResumen(Egreso egreso) {
        return EgresoResumenDTO.builder()
                .id(egreso.getId())
                .fecha(egreso.getFecha())
                .monto(egreso.getMonto())
                .montoIgv(egreso.getMontoIgv())
                .descripcion(egreso.getDescripcion())
                .nroComprobante(egreso.getNroComprobante())
                .tipoTributario(egreso.getTipoTributario().name())
                .tipoContabilidad(egreso.getTipoContabilidad().name())
                .build();
    }
    
    private DeclaracionResumenDTO mapDeclaracionResumen(Declaracion declaracion) {
        return DeclaracionResumenDTO.builder()
                .id(declaracion.getId())
                .periodoTributario(declaracion.getPeriodoTributario())
                .tipo(declaracion.getTipo())
                .estado(declaracion.getEstado() != null ? declaracion.getEstado().name() : null)
                .fechaLimite(declaracion.getFechaLimite())
                .totalIngresos(declaracion.getTotalIngresos())
                .totalEgresos(declaracion.getTotalEgresos())
                .utilidadEstimada(declaracion.getUtilidadEstimada())
                .igvVentas(declaracion.getIgvVentas())
                .igvCompras(declaracion.getIgvCompras())
                .irEstimado(declaracion.getIrEstimado())
                .totalPagarDeclaracion(declaracion.getTotalPagarDeclaracion())
                .build();
    }
    
    private ObligacionResumenDTO mapObligacionResumen(Obligacion obligacion) {
        return ObligacionResumenDTO.builder()
                .id(obligacion.getId())
                .tipo(obligacion.getTipo())
                .periodoTributario(obligacion.getPeriodoTributario())
                .monto(obligacion.getMonto())
                .fechaLimite(obligacion.getFechaLimite())
                .estado(obligacion.getEstado() != null ? obligacion.getEstado().name() : null)
                .observaciones(obligacion.getObservaciones())
                .build();
    }
    
    private PagoResumenDTO mapPagoResumen(Pago pago) {
        return PagoResumenDTO.builder()
                .id(pago.getId())
                .obligacionId(pago.getObligacion().getId())
                .montoPagado(pago.getMontoPagado())
                .fechaPago(pago.getFechaPago())
                .medioPago(pago.getMedioPago() != null ? pago.getMedioPago().name() : null)
                .estado(pago.getEstado() != null ? pago.getEstado().name() : null)
                .pagadoPor(pago.getPagadoPor() != null ? pago.getPagadoPor().name() : null)
                .comentarioContador(pago.getComentarioContador())
                .build();
    }
}
