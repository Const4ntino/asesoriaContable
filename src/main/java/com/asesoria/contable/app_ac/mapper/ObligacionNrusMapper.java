package com.asesoria.contable.app_ac.mapper;

import com.asesoria.contable.app_ac.model.dto.ObligacionNrusRequest;
import com.asesoria.contable.app_ac.model.dto.ObligacionNrusResponse;
import com.asesoria.contable.app_ac.model.entity.Cliente;
import com.asesoria.contable.app_ac.model.entity.ObligacionNrus;
import org.springframework.stereotype.Component;

@Component
public class ObligacionNrusMapper {

    public ObligacionNrus convertToEntity(ObligacionNrusRequest request) {
        ObligacionNrus entity = new ObligacionNrus();
        Cliente cliente = new Cliente();
        cliente.setId(request.getIdCliente());
        entity.setCliente(cliente);
        entity.setPeriodoTributario(request.getPeriodoTributario());
        entity.setTipo(request.getTipo());
        entity.setMonto(request.getMonto());
        entity.setFechaLimite(request.getFechaLimite());
        entity.setEstado(request.getEstado());
        entity.setObservaciones(request.getObservaciones());
        return entity;
    }

    public ObligacionNrusResponse convertToDto(ObligacionNrus entity) {
        ObligacionNrusResponse dto = new ObligacionNrusResponse();
        dto.setId(entity.getId());
        dto.setIdCliente(entity.getCliente().getId());
        dto.setPeriodoTributario(entity.getPeriodoTributario());
        dto.setTipo(entity.getTipo());
        dto.setMonto(entity.getMonto());
        dto.setFechaLimite(entity.getFechaLimite());
        dto.setEstado(entity.getEstado());
        dto.setObservaciones(entity.getObservaciones());
        return dto;
    }
}
