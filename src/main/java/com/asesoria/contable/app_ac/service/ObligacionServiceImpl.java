package com.asesoria.contable.app_ac.service;

import com.asesoria.contable.app_ac.exceptions.ClienteNotFoundException;
import com.asesoria.contable.app_ac.exceptions.DeclaracionNotFoundException;
import com.asesoria.contable.app_ac.exceptions.ObligacionNotFoundException;
import com.asesoria.contable.app_ac.mapper.ObligacionMapper;
import com.asesoria.contable.app_ac.model.dto.DeclaracionRequest;
import com.asesoria.contable.app_ac.model.dto.ObligacionRequest;
import com.asesoria.contable.app_ac.model.dto.ObligacionResponse;
import com.asesoria.contable.app_ac.model.entity.*;
import com.asesoria.contable.app_ac.repository.ClienteRepository;
import com.asesoria.contable.app_ac.repository.DeclaracionRepository;
import com.asesoria.contable.app_ac.repository.ContadorRepository;
import com.asesoria.contable.app_ac.repository.ObligacionRepository;
import com.asesoria.contable.app_ac.utils.enums.EstadoObligacion;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ObligacionServiceImpl implements ObligacionService {

    private final ObligacionRepository obligacionRepository;
    private final ObligacionMapper obligacionMapper;
    private final ClienteRepository clienteRepository;
    private final DeclaracionRepository declaracionRepository;
    private final ContadorRepository contadorRepository;

    @Override
    public List<ObligacionResponse> findAll() {
        return obligacionRepository.findAll().stream()
                .map(obligacionMapper::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public ObligacionResponse findById(Long id) {
        Obligacion obligacion = obligacionRepository.findById(id)
                .orElseThrow(() -> new ObligacionNotFoundException("Obligacion no encontrada con id: " + id));
        return obligacionMapper.convertToResponse(obligacion);
    }

    @Override
    public ObligacionResponse save(ObligacionRequest obligacionRequest) {
        Cliente cliente = clienteRepository.findById(obligacionRequest.getIdCliente())
                .orElseThrow(ClienteNotFoundException::new);

        Declaracion declaracion = null;
        if (obligacionRequest.getIdDeclaracion() != null) {
            declaracion = declaracionRepository.findById(obligacionRequest.getIdDeclaracion())
                    .orElseThrow(DeclaracionNotFoundException::new);
        }

        Obligacion obligacion = obligacionMapper.convertToEntity(obligacionRequest);
        obligacion.setCliente(cliente);
        obligacion.setDeclaracion(declaracion);

        return obligacionMapper.convertToResponse(obligacionRepository.save(obligacion));
    }

    @Override
    public ObligacionResponse update(Long id, ObligacionRequest obligacionRequest) {
        Obligacion obligacion = obligacionRepository.findById(id)
                .orElseThrow(() -> new ObligacionNotFoundException("Obligacion no encontrada con id: " + id));

        Cliente cliente = clienteRepository.findById(obligacionRequest.getIdCliente())
                .orElseThrow(ClienteNotFoundException::new);

        Declaracion declaracion = null;
        if (obligacionRequest.getIdDeclaracion() != null) {
            declaracion = declaracionRepository.findById(obligacionRequest.getIdDeclaracion())
                    .orElseThrow(DeclaracionNotFoundException::new);
        }

        obligacionMapper.updateEntity(obligacionRequest, obligacion);
        obligacion.setCliente(cliente);
        obligacion.setDeclaracion(declaracion);

        return obligacionMapper.convertToResponse(obligacionRepository.save(obligacion));
    }

    @Override
    public void deleteById(Long id) {
        if (!obligacionRepository.existsById(id)) {
            throw new ObligacionNotFoundException("Obligacion no encontrada con id: " + id);
        }
        obligacionRepository.deleteById(id);
    }

    @Override
    public List<ObligacionResponse> findByClienteId(Long clienteId) {
        if (!clienteRepository.existsById(clienteId)) {
            throw new ClienteNotFoundException();
        }
        return obligacionRepository.findByClienteId(clienteId).stream()
                .map(obligacionMapper::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<ObligacionResponse> getLatestObligacionesForMyClients(Usuario usuario) {
        Contador contador = contadorRepository.findByUsuarioId(usuario.getId())
                .orElseThrow(() -> new RuntimeException("Contador no encontrado para el usuario."));

        List<Cliente> clientes = clienteRepository.findAllByContadorId(contador.getId());
        List<Long> clienteIds = clientes.stream()
                .map(Cliente::getId)
                .collect(Collectors.toList());

        if (clienteIds.isEmpty()) {
            return new ArrayList<>();
        }

        List<Obligacion> latestObligaciones = obligacionRepository.findLatestObligacionesForClients(clienteIds);

        return latestObligaciones.stream()
                .map(obligacionMapper::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public ObligacionResponse saveFromDeclaracion(DeclaracionRequest declaracionRequest) {
        Cliente cliente = clienteRepository.findById(declaracionRequest.getIdCliente())
                .orElseThrow(ClienteNotFoundException::new);

        Obligacion obligacion = new Obligacion();
        obligacion.setCliente(cliente);
        obligacion.setTipo(declaracionRequest.getTipo());
        obligacion.setPeriodo(declaracionRequest.getPeriodoTributario());
        obligacion.setMonto(declaracionRequest.getTotalPagarDeclaracion());
        obligacion.setFechaLimite(declaracionRequest.getFechaLimite());
        obligacion.setEstado(EstadoObligacion.PENDIENTE);

        return obligacionMapper.convertToResponse(obligacionRepository.save(obligacion));
    }
}

