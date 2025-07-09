package com.asesoria.contable.app_ac.service;

import com.asesoria.contable.app_ac.model.dto.EgresoRequest;
import com.asesoria.contable.app_ac.model.dto.EgresoResponse;
import com.asesoria.contable.app_ac.model.entity.Usuario;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

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
}
