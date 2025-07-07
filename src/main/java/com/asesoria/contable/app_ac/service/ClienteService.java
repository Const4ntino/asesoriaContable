package com.asesoria.contable.app_ac.service;

import com.asesoria.contable.app_ac.model.dto.ClienteRequest;
import com.asesoria.contable.app_ac.model.dto.ClienteResponse;

import java.util.List;

public interface ClienteService {

    ClienteResponse findById(Long id);
    List<ClienteResponse> findAll();
    ClienteResponse findByUsuarioId(Long usuarioId);
    List<ClienteResponse> findAllByContadorId(Long contadorId);
    ClienteResponse save(ClienteRequest request);
    ClienteResponse update(Long id, ClienteRequest request);
    void deleteById(Long id);
    ClienteResponse asignarContador(Long clienteId, Long contadorId);
    void desasignarContador(Long clienteId);
    List<ClienteResponse> searchClientes(String searchTerm, String tipoCliente, String regimen, String sortBy, String sortOrder);
}
