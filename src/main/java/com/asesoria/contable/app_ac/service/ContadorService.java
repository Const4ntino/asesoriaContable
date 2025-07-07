package com.asesoria.contable.app_ac.service;

import com.asesoria.contable.app_ac.model.dto.ContadorRequest;
import com.asesoria.contable.app_ac.model.dto.ContadorResponse;

import java.util.List;

public interface ContadorService {

    ContadorResponse findById(Long id);
    List<ContadorResponse> findAll();
    ContadorResponse findByUsuarioId(Long usuarioId);
    ContadorResponse save(ContadorRequest request);
    ContadorResponse update(Long id, ContadorRequest request);
    void deleteById(Long id);
    List<ContadorResponse> searchContadores(String searchTerm, String sortBy, String sortOrder);
}
