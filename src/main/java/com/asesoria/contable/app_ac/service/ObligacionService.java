package com.asesoria.contable.app_ac.service;

import com.asesoria.contable.app_ac.model.dto.DeclaracionRequest;
import com.asesoria.contable.app_ac.model.dto.ObligacionRequest;
import com.asesoria.contable.app_ac.model.dto.ObligacionResponse;
import com.asesoria.contable.app_ac.model.entity.Usuario;

import java.time.LocalDate;
import java.util.List;

public interface ObligacionService {

    List<ObligacionResponse> findAll();

    ObligacionResponse findById(Long id);

    ObligacionResponse save(ObligacionRequest obligacionRequest);

    ObligacionResponse saveFromDeclaracion(DeclaracionRequest declaracionRequest);

    ObligacionResponse update(Long id, ObligacionRequest obligacionRequest);

    void deleteById(Long id);

    List<ObligacionResponse> findByClienteId(Long clienteId);

    List<ObligacionResponse> buscarObligaciones(
            Usuario usuario,
            String estado,
            String nombreCliente,
            LocalDate desde,
            LocalDate hasta,
            String orden
    );

    List<ObligacionResponse> buscarMisObligaciones(Usuario usuario);
}
