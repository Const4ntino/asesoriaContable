package com.asesoria.contable.app_ac.service;

import com.asesoria.contable.app_ac.model.dto.DeclaracionRequest;
import com.asesoria.contable.app_ac.model.dto.DeclaracionResponse;

import java.util.List;

public interface DeclaracionService {

    DeclaracionResponse findById(Long id);
    List<DeclaracionResponse> findAll();
    DeclaracionResponse save(DeclaracionRequest request);
    DeclaracionResponse update(Long id, DeclaracionRequest request);
    void deleteById(Long id);
    List<DeclaracionResponse> findByClienteId(Long clienteId);
}
