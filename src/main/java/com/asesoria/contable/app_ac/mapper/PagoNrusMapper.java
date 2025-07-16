package com.asesoria.contable.app_ac.mapper;

import com.asesoria.contable.app_ac.model.dto.PagoNrusRequest;
import com.asesoria.contable.app_ac.model.dto.PagoNrusResponse;
import com.asesoria.contable.app_ac.model.entity.ObligacionNrus;
import com.asesoria.contable.app_ac.model.entity.PagoNrus;
import org.springframework.stereotype.Component;

@Component
public class PagoNrusMapper {

    public PagoNrus convertToEntity(PagoNrusRequest request) {
        PagoNrus entity = new PagoNrus();
        ObligacionNrus obligacionNrus = new ObligacionNrus();
        obligacionNrus.setId(request.getIdObligacionNrus());
        entity.setObligacionNrus(obligacionNrus);
        entity.setMontoPagado(request.getMontoPagado());
        entity.setFechaPago(request.getFechaPago());
        entity.setMedioPago(request.getMedioPago());
        entity.setUrlComprobante(request.getUrlComprobante());
        entity.setEstado(request.getEstado());
        entity.setPagadoPor(request.getPagadoPor());
        entity.setComentarioContador(request.getComentarioContador());
        return entity;
    }

    public PagoNrusResponse convertToDto(PagoNrus entity) {
        PagoNrusResponse dto = new PagoNrusResponse();
        dto.setId(entity.getId());
        dto.setIdObligacionNrus(entity.getObligacionNrus().getId());
        dto.setMontoPagado(entity.getMontoPagado());
        dto.setFechaPago(entity.getFechaPago());
        dto.setMedioPago(entity.getMedioPago());
        dto.setUrlComprobante(entity.getUrlComprobante());
        dto.setEstado(entity.getEstado());
        dto.setPagadoPor(entity.getPagadoPor());
        dto.setComentarioContador(entity.getComentarioContador());
        return dto;
    }
}
