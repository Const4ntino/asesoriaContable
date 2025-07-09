package com.asesoria.contable.app_ac.service;

import com.asesoria.contable.app_ac.model.dto.ObligacionRequest;
import com.asesoria.contable.app_ac.model.dto.ObligacionResponse;

import java.util.List;

public interface ObligacionService {

    List<ObligacionResponse> findAll();

    ObligacionResponse findById(Long id);

    ObligacionResponse save(ObligacionRequest obligacionRequest);

    ObligacionResponse update(Long id, ObligacionRequest obligacionRequest);

    void deleteById(Long id);

    List<ObligacionResponse> findByClienteId(Long clienteId);
}
