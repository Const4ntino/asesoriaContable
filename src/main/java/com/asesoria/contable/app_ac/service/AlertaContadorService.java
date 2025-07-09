package com.asesoria.contable.app_ac.service;

import com.asesoria.contable.app_ac.model.dto.AlertaContadorRequest;
import com.asesoria.contable.app_ac.model.dto.AlertaContadorResponse;

import java.util.List;

public interface AlertaContadorService {

    AlertaContadorResponse findById(Long id);
    List<AlertaContadorResponse> findAll();
    AlertaContadorResponse save(AlertaContadorRequest request);
    AlertaContadorResponse update(Long id, AlertaContadorRequest request);
    void deleteById(Long id);
}
