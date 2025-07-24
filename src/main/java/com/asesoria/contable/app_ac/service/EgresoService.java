package com.asesoria.contable.app_ac.service;

import com.asesoria.contable.app_ac.model.dto.EgresoRequest;
import com.asesoria.contable.app_ac.model.dto.EgresoResponse;
import com.asesoria.contable.app_ac.model.entity.Usuario;
import com.asesoria.contable.app_ac.utils.enums.TipoTributario;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface EgresoService {

    EgresoResponse findById(Long id);
    List<EgresoResponse> findAll();
    EgresoResponse save(EgresoRequest request);
    EgresoResponse update(Long id, EgresoRequest request);
    void deleteById(Long id);
    // ADMINISTRADOR, CONTADOR
    List<EgresoResponse> findByClienteId(Long usuarioId);
    // CLIENTE
    EgresoResponse saveByUsuario(EgresoRequest request, Usuario usuario);
    EgresoResponse updateMyEgreso(Long id, EgresoRequest request, Usuario usuario);
    void deleteMyEgreso(Long id, Usuario usuario);
    List<EgresoResponse> findByUsuarioId(Long usuarioId);
    
    // Filtrar egresos por usuario, mes y año
    List<EgresoResponse> findByUsuarioIdAndPeriodo(Long usuarioId, Integer mes, Integer anio);

    // PARA MÉTRICAS
    BigDecimal calcularTotalMesActual(Long clienteId);
    BigDecimal calcularTotalMesAnterior(Long clienteId);
    Map<String, BigDecimal> obtenerEgresosPorTipoContabilidad(Long clienteId);
    Map<String, BigDecimal> obtenerEgresosPorTipoTributario(Long clienteId);
    List<Map<String, Object>> identificarEgresosRecurrentes(Long clienteId);

    BigDecimal getSumaEgresosGravadosMesAnterior(Long clienteId);
    BigDecimal getSumaEgresosExoneradosMesAnterior(Long clienteId);
    BigDecimal getSumaEgresosInafectosMesAnterior(Long clienteId);
    BigDecimal getSumaIgvEgresosMesAnterior(Long clienteId);
    
    // Filtrar egresos por cliente con paginación y filtros
    Page<EgresoResponse> filtrarEgresos(
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
