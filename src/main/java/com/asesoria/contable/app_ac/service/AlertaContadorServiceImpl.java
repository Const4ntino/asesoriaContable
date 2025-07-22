package com.asesoria.contable.app_ac.service;

import com.asesoria.contable.app_ac.exceptions.AlertaContadorNotFoundException;
import com.asesoria.contable.app_ac.exceptions.ContadorNotFoundException;
import com.asesoria.contable.app_ac.mapper.AlertaContadorMapper;
import com.asesoria.contable.app_ac.model.dto.AlertaContadorRequest;
import com.asesoria.contable.app_ac.model.dto.AlertaContadorResponse;
import com.asesoria.contable.app_ac.model.entity.AlertaCliente;
import com.asesoria.contable.app_ac.model.entity.AlertaContador;
import com.asesoria.contable.app_ac.model.entity.Contador;
import com.asesoria.contable.app_ac.repository.AlertaContadorRepository;
import com.asesoria.contable.app_ac.repository.ContadorRepository;
import com.asesoria.contable.app_ac.utils.enums.EstadoAlerta;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AlertaContadorServiceImpl implements AlertaContadorService {

    private final AlertaContadorRepository alertaContadorRepository;
    private final ContadorRepository contadorRepository;
    private final AlertaContadorMapper alertaContadorMapper;

    @Override
    public AlertaContadorResponse findById(Long id) {
        return alertaContadorRepository.findById(id)
                .map(alertaContadorMapper::toAlertaContadorResponse)
                .orElseThrow(AlertaContadorNotFoundException::new);
    }

    @Override
    public List<AlertaContadorResponse> findAllByContadorId(Long id) {
        return alertaContadorRepository.findByContadorIdOrderByFechaCreacionDesc(id)
                .stream()
                .map(alertaContadorMapper::toAlertaContadorResponse)
                .toList();
    }

//    @Override
//    public List<AlertaContadorResponse> findAll() {
//        return alertaContadorRepository.findAll()
//                .stream()
//                .map(alertaContadorMapper::toAlertaContadorResponse)
//                .collect(Collectors.toList());
//    }

    @Override
    public AlertaContadorResponse save(AlertaContadorRequest request) {
        Contador contador = contadorRepository.findById(request.getIdContador())
                .orElseThrow(ContadorNotFoundException::new);

        AlertaContador alertaContador = alertaContadorMapper.toAlertaContador(request);
        alertaContador.setContador(contador);

        AlertaContador alertaGuardada = alertaContadorRepository.save(alertaContador);
        return alertaContadorMapper.toAlertaContadorResponse(alertaGuardada);
    }

    @Override
    public AlertaContador marcarComoVisto(Long idAlerta) {
        AlertaContador alerta = alertaContadorRepository.findById(idAlerta)
                .orElseThrow(() -> new RuntimeException("Alerta no encontrada"));
        alerta.setEstado(EstadoAlerta.VISTO);
        return alertaContadorRepository.save(alerta);
    }

    @Override
    public void marcarComoResuelto(Long idAlerta) {
        AlertaContador alerta = alertaContadorRepository.findById(idAlerta)
                .orElseThrow(() -> new RuntimeException("Alerta no encontrada"));
        alerta.setEstado(EstadoAlerta.RESUELTO);
        alertaContadorRepository.save(alerta);
        alertaContadorRepository.deleteById(idAlerta);
    }

//    @Override
//    public AlertaContadorResponse update(Long id, AlertaContadorRequest request) {
//        AlertaContador alertaContador = alertaContadorRepository.findById(id)
//                .orElseThrow(AlertaContadorNotFoundException::new);
//
//        Contador contador = contadorRepository.findById(request.getIdContador())
//                .orElseThrow(ContadorNotFoundException::new);
//
//        alertaContadorMapper.updateAlertaContadorFromRequest(request, alertaContador);
//        alertaContador.setContador(contador);
//
//        AlertaContador alertaActualizada = alertaContadorRepository.save(alertaContador);
//        return alertaContadorMapper.toAlertaContadorResponse(alertaActualizada);
//    }

//    @Override
//    public void deleteById(Long id) {
//        if (alertaContadorRepository.findById(id).isEmpty()) {
//            throw new AlertaContadorNotFoundException();
//        }
//        alertaContadorRepository.deleteById(id);
//    }
}
