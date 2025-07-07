package com.asesoria.contable.app_ac.service;

import com.asesoria.contable.app_ac.model.dto.UsuarioRequest;
import com.asesoria.contable.app_ac.model.dto.UsuarioResponse;
import com.asesoria.contable.app_ac.model.dto.UsuarioUpdateRequest;

import java.util.List;

public interface UsuarioService {

    UsuarioResponse findById(Long id);
    List<UsuarioResponse> findAll();
    List<UsuarioResponse> findUsuariosClientesLibres(String username);
    List<UsuarioResponse> findUsuariosContadoresLibres(String username);
    UsuarioResponse save(UsuarioRequest request);
    UsuarioResponse update(Long id, UsuarioRequest request);
    void deleteById(Long id);
    List<UsuarioResponse> searchUsuarios(String searchTerm, List<String> roles, Boolean estado, String sortBy, String sortOrder);
}
