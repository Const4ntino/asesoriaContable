package com.asesoria.contable.app_ac.service;

import com.asesoria.contable.app_ac.exceptions.ObligacionNrusNotFoundException;
import com.asesoria.contable.app_ac.exceptions.PagoNrusNotFoundException;
import com.asesoria.contable.app_ac.mapper.PagoNrusMapper;
import com.asesoria.contable.app_ac.model.dto.PagoNrusRequest;
import com.asesoria.contable.app_ac.model.dto.PagoNrusResponse;
import com.asesoria.contable.app_ac.model.entity.PagoNrus;
import com.asesoria.contable.app_ac.model.entity.ObligacionNrus;
import com.asesoria.contable.app_ac.repository.ObligacionNrusRepository;
import com.asesoria.contable.app_ac.repository.PagoNrusRepository;
import com.asesoria.contable.app_ac.utils.enums.EstadoObligacion;
import com.asesoria.contable.app_ac.utils.enums.EstadoPagoNrus;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class PagoNrusServiceImpl implements PagoNrusService {

    private final PagoNrusRepository pagoNrusRepository;
    private final PagoNrusMapper pagoNrusMapper;
    private final ObligacionNrusRepository  obligacionNrusRepository;

    @Override
    public List<PagoNrusResponse> getAll() {
        return pagoNrusRepository.findAll().stream()
                .map(pagoNrusMapper::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public PagoNrusResponse getOne(Long id) {
        return pagoNrusRepository.findById(id)
                .map(pagoNrusMapper::convertToDto)
                .orElseThrow(() -> new PagoNrusNotFoundException("Pago NRUS no encontrado"));
    }

    @Override
    public PagoNrusResponse save(PagoNrusRequest pagoNrusRequest) {
        PagoNrus entity = pagoNrusMapper.convertToEntity(pagoNrusRequest);
        return pagoNrusMapper.convertToDto(pagoNrusRepository.save(entity));
    }

    @Override
    public PagoNrusResponse update(Long id, PagoNrusRequest pagoNrusRequest) {
        PagoNrus pagoNrus = pagoNrusRepository.findById(id)
                .orElseThrow(() -> new PagoNrusNotFoundException("Pago NRUS no encontrado"));

        pagoNrus.setMontoPagado(pagoNrusRequest.getMontoPagado());
        pagoNrus.setFechaPago(pagoNrusRequest.getFechaPago());
        pagoNrus.setMedioPago(pagoNrusRequest.getMedioPago());
        pagoNrus.setUrlComprobante(pagoNrusRequest.getUrlComprobante());
        pagoNrus.setEstado(pagoNrusRequest.getEstado());
        pagoNrus.setPagadoPor(pagoNrusRequest.getPagadoPor());
        pagoNrus.setComentarioContador(pagoNrusRequest.getComentarioContador());

        return pagoNrusMapper.convertToDto(pagoNrusRepository.save(pagoNrus));
    }

    @Override
    public void delete(Long id) {
        pagoNrusRepository.deleteById(id);
    }

    @Override
    public PagoNrusResponse registrarPago(Long idObligacionNrus, PagoNrusRequest pagoNrusRequest) {
        ObligacionNrus obligacionNrus = obligacionNrusRepository.findById(idObligacionNrus)
                .orElseThrow(() -> new ObligacionNrusNotFoundException("Obligacion NRUS no encontrada para registrar pago"));

        PagoNrus pagoNrus = pagoNrusMapper.convertToEntity(pagoNrusRequest);
        pagoNrus.setObligacionNrus(obligacionNrus);

        // Actualizar el estado de la obligación a POR_VALIDAR
        obligacionNrus.setEstado(EstadoObligacion.POR_CONFIRMAR);
        obligacionNrusRepository.save(obligacionNrus);

        return pagoNrusMapper.convertToDto(pagoNrusRepository.save(pagoNrus));
    }
}
