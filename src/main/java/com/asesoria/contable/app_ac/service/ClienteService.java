package com.asesoria.contable.app_ac.service;

import com.asesoria.contable.app_ac.model.dto.ClienteRequest;
import com.asesoria.contable.app_ac.model.dto.ClienteResponse;
import com.asesoria.contable.app_ac.model.entity.Cliente;

import java.util.List;

import com.asesoria.contable.app_ac.model.dto.MetricasDeclaracionResponse;

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
    // Para devolver un cliente y no un cliente response
    Cliente findEntityByUsuarioId(Long usuarioId);
    MetricasDeclaracionResponse getMetricasDeclaracion(Long clienteId);
}
