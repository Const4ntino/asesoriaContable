package com.asesoria.contable.app_ac.service;

import com.asesoria.contable.app_ac.model.dto.AlertaClienteRequest;
import com.asesoria.contable.app_ac.model.dto.AlertaClienteResponse;

import java.util.List;

public interface AlertaClienteService {

//    AlertaClienteResponse update(Long id, AlertaClienteRequest request);
//    void deleteById(Long id);
    AlertaClienteResponse findById(Long id);
    List<AlertaClienteResponse> findAllByClienteId(Long id);
    AlertaClienteResponse save(AlertaClienteRequest request);
    void marcarComoVisto(Long idAlerta);
    void marcarComoResuelto(Long idAlerta);
}
