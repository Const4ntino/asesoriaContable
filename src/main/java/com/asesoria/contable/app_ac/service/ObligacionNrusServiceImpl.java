package com.asesoria.contable.app_ac.service;

import com.asesoria.contable.app_ac.exceptions.ObligacionNrusNotFoundException;
import com.asesoria.contable.app_ac.mapper.ObligacionNrusMapper;
import com.asesoria.contable.app_ac.model.dto.ObligacionNrusRequest;
import com.asesoria.contable.app_ac.model.dto.ObligacionNrusResponse;
import com.asesoria.contable.app_ac.model.entity.ObligacionNrus;
import com.asesoria.contable.app_ac.repository.ObligacionNrusRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class ObligacionNrusServiceImpl implements ObligacionNrusService {

    private final ObligacionNrusRepository obligacionNrusRepository;
    private final ObligacionNrusMapper obligacionNrusMapper;

    @Override
    public List<ObligacionNrusResponse> getAll() {
        return obligacionNrusRepository.findAll().stream()
                .map(obligacionNrusMapper::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public ObligacionNrusResponse getOne(Long id) {
        return obligacionNrusRepository.findById(id)
                .map(obligacionNrusMapper::convertToDto)
                .orElseThrow(() -> new ObligacionNrusNotFoundException("Obligacion NRUS no encontrada"));
    }

    @Override
    public ObligacionNrusResponse save(ObligacionNrusRequest obligacionNrusRequest) {
        ObligacionNrus entity = obligacionNrusMapper.convertToEntity(obligacionNrusRequest);
        return obligacionNrusMapper.convertToDto(obligacionNrusRepository.save(entity));
    }

    @Override
    public ObligacionNrusResponse update(Long id, ObligacionNrusRequest obligacionNrusRequest) {
        ObligacionNrus obligacionNrus = obligacionNrusRepository.findById(id)
                .orElseThrow(() -> new ObligacionNrusNotFoundException("Obligacion NRUS no encontrada"));

        obligacionNrus.setPeriodoTributario(obligacionNrusRequest.getPeriodoTributario());
        obligacionNrus.setTipo(obligacionNrusRequest.getTipo());
        obligacionNrus.setMonto(obligacionNrusRequest.getMonto());
        obligacionNrus.setFechaLimite(obligacionNrusRequest.getFechaLimite());
        obligacionNrus.setEstado(obligacionNrusRequest.getEstado());
        obligacionNrus.setObservaciones(obligacionNrusRequest.getObservaciones());

        return obligacionNrusMapper.convertToDto(obligacionNrusRepository.save(obligacionNrus));
    }

    @Override
    public void delete(Long id) {
        obligacionNrusRepository.deleteById(id);
    }
}
