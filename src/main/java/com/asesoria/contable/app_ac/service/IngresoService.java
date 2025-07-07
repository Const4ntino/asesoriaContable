package com.asesoria.contable.app_ac.service;

import com.asesoria.contable.app_ac.model.dto.IngresoRequest;
import com.asesoria.contable.app_ac.model.dto.IngresoResponse;

import java.util.List;

public interface IngresoService {

    IngresoResponse findById(Long id);
    List<IngresoResponse> findAll();
    IngresoResponse save(IngresoRequest request);
    IngresoResponse update(Long id, IngresoRequest request);
    void deleteById(Long id);
}
