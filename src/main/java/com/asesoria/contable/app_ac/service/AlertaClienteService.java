package com.asesoria.contable.app_ac.service;

import com.asesoria.contable.app_ac.model.dto.AlertaClienteRequest;
import com.asesoria.contable.app_ac.model.dto.AlertaClienteResponse;

import java.util.List;

public interface AlertaClienteService {

    AlertaClienteResponse findById(Long id);
    List<AlertaClienteResponse> findAll();
    AlertaClienteResponse save(AlertaClienteRequest request);
    AlertaClienteResponse update(Long id, AlertaClienteRequest request);
    void deleteById(Long id);
}
