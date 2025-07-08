package com.asesoria.contable.app_ac.service;

import com.asesoria.contable.app_ac.exceptions.ClienteNotFoundException;
import com.asesoria.contable.app_ac.exceptions.DeclaracionNotFoundException;
import com.asesoria.contable.app_ac.mapper.DeclaracionMapper;
import com.asesoria.contable.app_ac.model.dto.DeclaracionRequest;
import com.asesoria.contable.app_ac.model.dto.DeclaracionResponse;
import com.asesoria.contable.app_ac.model.entity.Cliente;
import com.asesoria.contable.app_ac.model.entity.Declaracion;
import com.asesoria.contable.app_ac.repository.ClienteRepository;
import com.asesoria.contable.app_ac.repository.DeclaracionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DeclaracionServiceImpl implements DeclaracionService {

    private final DeclaracionRepository declaracionRepository;
    private final ClienteRepository clienteRepository;
    private final DeclaracionMapper declaracionMapper;

    @Override
    public DeclaracionResponse findById(Long id) {
        return declaracionRepository.findById(id)
                .map(declaracionMapper::toDeclaracionResponse)
                .orElseThrow(DeclaracionNotFoundException::new);
    }

    @Override
    public List<DeclaracionResponse> findAll() {
        return declaracionRepository.findAll()
                .stream()
                .map(declaracionMapper::toDeclaracionResponse)
                .collect(Collectors.toList());
    }

    @Override
    public DeclaracionResponse save(DeclaracionRequest request) {
        Cliente cliente = clienteRepository.findById(request.getIdCliente())
                .orElseThrow(ClienteNotFoundException::new);

        Declaracion declaracion = declaracionMapper.toDeclaracion(request);
        declaracion.setCliente(cliente);

        Declaracion declaracionGuardada = declaracionRepository.save(declaracion);
        return declaracionMapper.toDeclaracionResponse(declaracionGuardada);
    }

    @Override
    public DeclaracionResponse update(Long id, DeclaracionRequest request) {
        Declaracion declaracion = declaracionRepository.findById(id)
                .orElseThrow(DeclaracionNotFoundException::new);

        Cliente cliente = clienteRepository.findById(request.getIdCliente())
                .orElseThrow(ClienteNotFoundException::new);

        declaracionMapper.updateDeclaracionFromRequest(request, declaracion);
        declaracion.setCliente(cliente);

        Declaracion declaracionActualizada = declaracionRepository.save(declaracion);
        return declaracionMapper.toDeclaracionResponse(declaracionActualizada);
    }

    @Override
    public void deleteById(Long id) {
        if (declaracionRepository.findById(id).isEmpty()) {
            throw new DeclaracionNotFoundException();
        }
        declaracionRepository.deleteById(id);
    }

    @Override
    public List<DeclaracionResponse> findByClienteId(Long clienteId) {
        return declaracionRepository.findByClienteId(clienteId)
                .stream()
                .map(declaracionMapper::toDeclaracionResponse)
                .collect(Collectors.toList());
    }
}
