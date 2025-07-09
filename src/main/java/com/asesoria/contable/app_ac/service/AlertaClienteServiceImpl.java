package com.asesoria.contable.app_ac.service;

import com.asesoria.contable.app_ac.exceptions.AlertaClienteNotFoundException;
import com.asesoria.contable.app_ac.exceptions.ClienteNotFoundException;
import com.asesoria.contable.app_ac.mapper.AlertaClienteMapper;
import com.asesoria.contable.app_ac.model.dto.AlertaClienteRequest;
import com.asesoria.contable.app_ac.model.dto.AlertaClienteResponse;
import com.asesoria.contable.app_ac.model.entity.AlertaCliente;
import com.asesoria.contable.app_ac.model.entity.Cliente;
import com.asesoria.contable.app_ac.repository.AlertaClienteRepository;
import com.asesoria.contable.app_ac.repository.ClienteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AlertaClienteServiceImpl implements AlertaClienteService {

    private final AlertaClienteRepository alertaClienteRepository;
    private final ClienteRepository clienteRepository;
    private final AlertaClienteMapper alertaClienteMapper;

    @Override
    public AlertaClienteResponse findById(Long id) {
        return alertaClienteRepository.findById(id)
                .map(alertaClienteMapper::toAlertaClienteResponse)
                .orElseThrow(AlertaClienteNotFoundException::new);
    }

    @Override
    public List<AlertaClienteResponse> findAll() {
        return alertaClienteRepository.findAll()
                .stream()
                .map(alertaClienteMapper::toAlertaClienteResponse)
                .collect(Collectors.toList());
    }

    @Override
    public AlertaClienteResponse save(AlertaClienteRequest request) {
        Cliente cliente = clienteRepository.findById(request.getIdCliente())
                .orElseThrow(ClienteNotFoundException::new);

        AlertaCliente alertaCliente = alertaClienteMapper.toAlertaCliente(request);
        alertaCliente.setCliente(cliente);

        AlertaCliente alertaGuardada = alertaClienteRepository.save(alertaCliente);
        return alertaClienteMapper.toAlertaClienteResponse(alertaGuardada);
    }

    @Override
    public AlertaClienteResponse update(Long id, AlertaClienteRequest request) {
        AlertaCliente alertaCliente = alertaClienteRepository.findById(id)
                .orElseThrow(AlertaClienteNotFoundException::new);

        Cliente cliente = clienteRepository.findById(request.getIdCliente())
                .orElseThrow(ClienteNotFoundException::new);

        alertaClienteMapper.updateAlertaClienteFromRequest(request, alertaCliente);
        alertaCliente.setCliente(cliente);

        AlertaCliente alertaActualizada = alertaClienteRepository.save(alertaCliente);
        return alertaClienteMapper.toAlertaClienteResponse(alertaActualizada);
    }

    @Override
    public void deleteById(Long id) {
        if (alertaClienteRepository.findById(id).isEmpty()) {
            throw new AlertaClienteNotFoundException();
        }
        alertaClienteRepository.deleteById(id);
    }
}
