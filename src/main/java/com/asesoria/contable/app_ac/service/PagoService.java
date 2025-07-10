package com.asesoria.contable.app_ac.service;

import com.asesoria.contable.app_ac.model.dto.PagoRequest;
import com.asesoria.contable.app_ac.model.dto.PagoResponse;

import java.util.List;

public interface PagoService {

    PagoResponse findById(Long id);
    List<PagoResponse> findAll();
    PagoResponse save(PagoRequest request);
    PagoResponse update(Long id, PagoRequest request);
    void deleteById(Long id);
}
