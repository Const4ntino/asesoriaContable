package com.asesoria.contable.app_ac.service;

import com.asesoria.contable.app_ac.exceptions.ObligacionNotFoundException;
import com.asesoria.contable.app_ac.exceptions.PagoNotFoundException;
import com.asesoria.contable.app_ac.mapper.PagoMapper;
import com.asesoria.contable.app_ac.model.dto.PagoRequest;
import com.asesoria.contable.app_ac.model.dto.PagoResponse;
import com.asesoria.contable.app_ac.model.entity.Obligacion;
import com.asesoria.contable.app_ac.model.entity.Pago;
import com.asesoria.contable.app_ac.repository.ObligacionRepository;
import com.asesoria.contable.app_ac.repository.PagoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PagoServiceImpl implements PagoService {

    private final PagoRepository pagoRepository;
    private final ObligacionRepository obligacionRepository;
    private final PagoMapper pagoMapper;

    @Override
    public PagoResponse findById(Long id) {
        return pagoRepository.findById(id)
                .map(pagoMapper::toPagoResponse)
                .orElseThrow(PagoNotFoundException::new);
    }

    @Override
    public List<PagoResponse> findAll() {
        return pagoRepository.findAll()
                .stream()
                .map(pagoMapper::toPagoResponse)
                .collect(Collectors.toList());
    }

    @Override
    public PagoResponse save(PagoRequest request) {
        Obligacion obligacion = obligacionRepository.findById(request.getIdObligacion())
                .orElseThrow(() -> new ObligacionNotFoundException("Obligación no encontrada con ID: " + request.getIdObligacion()));

        Pago pago = pagoMapper.toPago(request);
        pago.setObligacion(obligacion);

        Pago pagoGuardado = pagoRepository.save(pago);
        return pagoMapper.toPagoResponse(pagoGuardado);
    }

    @Override
    public PagoResponse update(Long id, PagoRequest request) {
        Pago pago = pagoRepository.findById(id)
                .orElseThrow(PagoNotFoundException::new);

        Obligacion obligacion = obligacionRepository.findById(request.getIdObligacion())
                .orElseThrow(() -> new ObligacionNotFoundException("Obligación no encontrada con ID: " + request.getIdObligacion()));

        pagoMapper.updatePagoFromRequest(request, pago);
        pago.setObligacion(obligacion);

        Pago pagoActualizado = pagoRepository.save(pago);
        return pagoMapper.toPagoResponse(pagoActualizado);
    }

    @Override
    public void deleteById(Long id) {
        if (pagoRepository.findById(id).isEmpty()) {
            throw new PagoNotFoundException();
        }
        pagoRepository.deleteById(id);
    }
}
