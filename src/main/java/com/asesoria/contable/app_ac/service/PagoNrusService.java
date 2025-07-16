package com.asesoria.contable.app_ac.service;

import com.asesoria.contable.app_ac.model.dto.PagoNrusRequest;
import com.asesoria.contable.app_ac.model.dto.PagoNrusResponse;

import java.util.List;

public interface PagoNrusService {

    List<PagoNrusResponse> getAll();

    PagoNrusResponse getOne(Long id);

    PagoNrusResponse save(PagoNrusRequest pagoNrusRequest);

    PagoNrusResponse update(Long id, PagoNrusRequest pagoNrusRequest);

    void delete(Long id);

    PagoNrusResponse registrarPago(Long idObligacionNrus, PagoNrusRequest pagoNrusRequest);
}
