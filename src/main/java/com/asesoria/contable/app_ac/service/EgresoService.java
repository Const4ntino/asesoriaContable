package com.asesoria.contable.app_ac.service;

import com.asesoria.contable.app_ac.model.dto.EgresoRequest;
import com.asesoria.contable.app_ac.model.dto.EgresoResponse;
import com.asesoria.contable.app_ac.model.dto.IngresoRequest;
import com.asesoria.contable.app_ac.model.dto.IngresoResponse;
import com.asesoria.contable.app_ac.model.entity.Usuario;

import java.util.List;

public interface EgresoService {

    EgresoResponse findById(Long id);
    List<EgresoResponse> findAll();
    EgresoResponse save(EgresoRequest request);
    EgresoResponse update(Long id, EgresoRequest request);
    void deleteById(Long id);
    // ADMINISTRADOR, CONTADOR
    List<EgresoResponse> findByClienteId(Long usuarioId);
    // CLIENTE
    EgresoResponse saveByUsuario(EgresoRequest request, Usuario usuario);
    EgresoResponse updateMyEgreso(Long id, EgresoRequest request, Usuario usuario);
    void deleteMyEgreso(Long id, Usuario usuario);
    List<EgresoResponse> findByUsuarioId(Long usuarioId);
}
