package com.asesoria.contable.app_ac.service;

import com.asesoria.contable.app_ac.model.dto.AlertaClienteRequest;
import com.asesoria.contable.app_ac.model.dto.AlertaClienteResponse;
import com.asesoria.contable.app_ac.model.dto.AlertaContadorRequest;
import com.asesoria.contable.app_ac.model.dto.AlertaContadorResponse;
import com.asesoria.contable.app_ac.model.entity.AlertaContador;

import java.util.List;

public interface AlertaContadorService {

    AlertaContadorResponse findById(Long id);
    List<AlertaContadorResponse> findAllByContadorId(Long id);
    AlertaContadorResponse save(AlertaContadorRequest request);
    AlertaContador marcarComoVisto(Long idAlerta);
    void marcarComoResuelto(Long idAlerta);
}
