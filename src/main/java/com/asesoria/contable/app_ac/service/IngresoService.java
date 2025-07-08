package com.asesoria.contable.app_ac.service;

import com.asesoria.contable.app_ac.model.dto.IngresoRequest;
import com.asesoria.contable.app_ac.model.dto.IngresoResponse;
import com.asesoria.contable.app_ac.model.entity.Usuario;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

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

    BigDecimal calcularTotalMesActual(Long clienteId);
    BigDecimal calcularTotalMesAnterior(Long clienteId);
    Map<String, BigDecimal> obtenerIngresosPorCategoria(Long clienteId);
    Long contarComprobantesMesActual(Long clienteId);
    Map<String, BigDecimal> obtenerIngresosPorTipoTributario(Long clienteId);
    List<Map<String, Object>> identificarIngresosRecurrentes(Long clienteId);
}
