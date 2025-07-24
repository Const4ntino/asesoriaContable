package com.asesoria.contable.app_ac.service;

import com.asesoria.contable.app_ac.model.dto.IngresoRequest;
import com.asesoria.contable.app_ac.model.dto.IngresoResponse;
import com.asesoria.contable.app_ac.model.entity.Usuario;
import com.asesoria.contable.app_ac.utils.enums.TipoTributario;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface IngresoService {

    IngresoResponse findById(Long id);
    List<IngresoResponse> findAll();
    IngresoResponse save(IngresoRequest request);
    IngresoResponse update(Long id, IngresoRequest request);
    void deleteById(Long id);
    IngresoResponse saveByUsuario(IngresoRequest request, Usuario usuario);
    IngresoResponse updateMyIngreso(Long id, IngresoRequest request, Usuario usuario);
    void deleteMyIngreso(Long id, Usuario usuario);
    List<IngresoResponse> findByClienteId(Long clienteId);
    List<IngresoResponse> findByClienteIdAndFechaBetween(Long clienteId, LocalDate startDate, LocalDate endDate);
    List<IngresoResponse> findByUsuarioId(Long usuarioId);
    
    /**
     * Busca ingresos por usuario con filtros opcionales de mes y año
     * @param usuarioId ID del usuario
     * @param mes Mes opcional para filtrar (1-12)
     * @param anio Año opcional para filtrar
     * @return Lista de ingresos filtrados
     */
    List<IngresoResponse> findByUsuarioIdAndPeriodo(Long usuarioId, Integer mes, Integer anio);

    BigDecimal calcularTotalMesActual(Long clienteId);
    BigDecimal calcularTotalMesAnterior(Long clienteId);
    Map<String, BigDecimal> obtenerIngresosPorCategoria(Long clienteId);
    Long contarComprobantesMesActual(Long clienteId);
    Map<String, BigDecimal> obtenerIngresosPorTipoTributario(Long clienteId);
    List<Map<String, Object>> identificarIngresosRecurrentes(Long clienteId);

    BigDecimal getSumaIngresosGravadosMesAnterior(Long clienteId);
    BigDecimal getSumaIngresosExoneradosMesAnterior(Long clienteId);
    BigDecimal getSumaIngresosInafectosMesAnterior(Long clienteId);
    BigDecimal getSumaIgvIngresosMesAnterior(Long clienteId);
    
    // Nuevo método para filtrar ingresos con múltiples criterios
    Page<IngresoResponse> filtrarIngresos(
            Long clienteId,
            BigDecimal montoMinimo,
            BigDecimal montoMaximo,
            LocalDate fechaInicio,
            LocalDate fechaFin,
            Integer mes,
            Integer anio,
            TipoTributario tipoTributario,
            String descripcion,
            String nroComprobante,
            Pageable pageable);
}
