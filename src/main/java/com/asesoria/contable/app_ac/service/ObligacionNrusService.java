package com.asesoria.contable.app_ac.service;

import com.asesoria.contable.app_ac.model.dto.ObligacionNrusRequest;
import com.asesoria.contable.app_ac.model.dto.ObligacionNrusResponse;
import com.asesoria.contable.app_ac.utils.enums.EstadoObligacion;

import java.time.LocalDate;
import java.util.List;

public interface ObligacionNrusService {

    List<ObligacionNrusResponse> getAll();

    ObligacionNrusResponse getOne(Long id);

    ObligacionNrusResponse save(ObligacionNrusRequest obligacionNrusRequest);

    ObligacionNrusResponse update(Long id, ObligacionNrusRequest obligacionNrusRequest);

    void delete(Long id);

    ObligacionNrusResponse generarOActualizarObligacionNrus(Long idCliente, LocalDate periodoTributario);

    List<ObligacionNrusResponse> getObligacionesByClienteId(Long idCliente, LocalDate periodo, EstadoObligacion estado);
}
