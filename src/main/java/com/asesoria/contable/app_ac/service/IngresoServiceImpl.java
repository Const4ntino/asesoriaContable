package com.asesoria.contable.app_ac.service;

import com.asesoria.contable.app_ac.exceptions.ClienteNotFoundException;
import com.asesoria.contable.app_ac.exceptions.IngresoNotFoundException;
import com.asesoria.contable.app_ac.mapper.IngresoMapper;
import com.asesoria.contable.app_ac.model.dto.IngresoRequest;
import com.asesoria.contable.app_ac.model.dto.IngresoResponse;
import com.asesoria.contable.app_ac.model.entity.Cliente;
import com.asesoria.contable.app_ac.model.entity.Ingreso;
import com.asesoria.contable.app_ac.repository.ClienteRepository;
import com.asesoria.contable.app_ac.repository.IngresoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class IngresoServiceImpl implements IngresoService {

    private final IngresoRepository ingresoRepository;
    private final ClienteRepository clienteRepository;
    private final IngresoMapper ingresoMapper;

    @Override
    public IngresoResponse findById(Long id) {
        return ingresoRepository.findById(id)
                .map(ingresoMapper::toIngresoResponse)
                .orElseThrow(IngresoNotFoundException::new);
    }

    @Override
    public List<IngresoResponse> findAll() {
        return ingresoRepository.findAll()
                .stream()
                .map(ingresoMapper::toIngresoResponse)
                .collect(Collectors.toList());
    }

    @Override
    public IngresoResponse save(IngresoRequest request) {
        Cliente cliente = clienteRepository.findById(request.getIdCliente())
                .orElseThrow(ClienteNotFoundException::new);

        Ingreso ingreso = ingresoMapper.toIngreso(request);
        ingreso.setCliente(cliente);

        Ingreso ingresoGuardado = ingresoRepository.save(ingreso);
        return ingresoMapper.toIngresoResponse(ingresoGuardado);
    }

    @Override
    public IngresoResponse update(Long id, IngresoRequest request) {
        Ingreso ingreso = ingresoRepository.findById(id)
                .orElseThrow(IngresoNotFoundException::new);

        Cliente cliente = clienteRepository.findById(request.getIdCliente())
                .orElseThrow(ClienteNotFoundException::new);

        ingresoMapper.updateIngresoFromRequest(request, ingreso);
        ingreso.setCliente(cliente);

        Ingreso ingresoActualizado = ingresoRepository.save(ingreso);
        return ingresoMapper.toIngresoResponse(ingresoActualizado);
    }

    @Override
    public void deleteById(Long id) {
        if (ingresoRepository.findById(id).isEmpty()) {
            throw new IngresoNotFoundException();
        }
        ingresoRepository.deleteById(id);
    }
}
