package com.asesoria.contable.app_ac.service;

import com.asesoria.contable.app_ac.exceptions.ClienteNotFoundException;
import com.asesoria.contable.app_ac.exceptions.ContadorNotFoundException;
import com.asesoria.contable.app_ac.exceptions.ObligacionNotFoundException;
import com.asesoria.contable.app_ac.exceptions.PagoNotFoundException;
import com.asesoria.contable.app_ac.mapper.ObligacionMapper;
import com.asesoria.contable.app_ac.mapper.PagoMapper;
import com.asesoria.contable.app_ac.model.dto.PagoClienteRequest;
import com.asesoria.contable.app_ac.model.dto.PagoContadorRequest;
import com.asesoria.contable.app_ac.model.dto.PagoRequest;
import com.asesoria.contable.app_ac.model.dto.PagoResponse;
import com.asesoria.contable.app_ac.model.entity.*;
import com.asesoria.contable.app_ac.repository.ClienteRepository;
import com.asesoria.contable.app_ac.repository.ContadorRepository;
import com.asesoria.contable.app_ac.service.DeclaracionServiceImpl.*;
import com.asesoria.contable.app_ac.repository.ObligacionRepository;
import com.asesoria.contable.app_ac.repository.PagoRepository;
import com.asesoria.contable.app_ac.utils.enums.EstadoObligacion;
import com.asesoria.contable.app_ac.utils.enums.EstadoPago;
import com.asesoria.contable.app_ac.utils.enums.MedioPago;
import com.asesoria.contable.app_ac.utils.enums.PagadoPor;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import javax.swing.text.html.Option;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class PagoServiceImpl implements PagoService {

    private final PagoRepository pagoRepository;
    private final ObligacionRepository obligacionRepository;
    private final ClienteRepository clienteRepository;
    private final ContadorRepository contadorRepository;
    private final PagoMapper pagoMapper;
    private final ObligacionMapper obligacionMapper;

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
    public PagoResponse registrarPagoCliente(Long idObligacion, PagoClienteRequest pagoRequest) {
        Obligacion obligacion = obligacionRepository.findById(idObligacion)
                .orElseThrow(() -> new ObligacionNotFoundException("Obligacion no encontrada con ID: " + idObligacion));

        Pago pagoGuardado = new Pago();
        pagoGuardado.setObligacion(obligacion);
        pagoGuardado.setMontoPagado(obligacion.getMonto());
        pagoGuardado.setFechaPago(pagoRequest.getFechaPago());
        pagoGuardado.setMedioPago(pagoRequest.getMedioPago());
        pagoGuardado.setUrlVoucher(pagoRequest.getUrlVoucher());
        pagoGuardado.setEstado(EstadoPago.POR_VALIDAR);
        pagoGuardado.setPagadoPor(PagadoPor.CLIENTE);

        pagoGuardado = pagoRepository.save(pagoGuardado);

        obligacion.setEstado(EstadoObligacion.POR_CONFIRMAR);
        obligacionRepository.save(obligacion);

        return pagoMapper.toPagoResponse(pagoGuardado);
    }


    @Override
    public void rechazarPagoCliente(Long idPago, String comentario) {
        Pago pago = pagoRepository.findById(idPago)
                .orElseThrow(() -> new ObligacionNotFoundException("Obligacion no encontrada con ID: " + idPago));

        Optional<Obligacion> obligacion = obligacionRepository.findById(pago.getObligacion().getId());
        Obligacion obligacionActual = obligacion
                .orElseThrow(() -> new ObligacionNotFoundException("Obligación no encontrada con ID: " + pago.getObligacion().getId()));
        try {
            // Actualizas solo lo necesario
            pago.setEstado(EstadoPago.RECHAZADO);
            pago.setComentarioContador(comentario);

            if (pago.getUrlVoucher() != null && !pago.getUrlVoucher().isBlank()) {
                DeclaracionServiceImpl.eliminarArchivoAnterior(pago.getUrlVoucher());
            }

            obligacionActual.setEstado(EstadoObligacion.PENDIENTE);

            obligacionRepository.save(obligacionActual);

            pagoRepository.delete(pago);
        } catch (DataIntegrityViolationException ex) {
            throw new RuntimeException("Error de integridad al rechazar el pago.", ex);
        } catch (Exception ex) {
            throw new RuntimeException("Error inesperado al rechazar el pago.", ex);
        }
    }


    @Override
    public PagoResponse registrarPagoContador(Long idObligacion, PagoContadorRequest pagoRequest) {
        Obligacion obligacion = obligacionRepository.findById(idObligacion)
                .orElseThrow(() -> new ObligacionNotFoundException("Obligacion no encontrada con ID: " + idObligacion));

        try {
            Pago pago = new Pago();
            pago.setObligacion(obligacion);
            pago.setMontoPagado(obligacion.getMonto());
            pago.setFechaPago(pagoRequest.getFechaPago());
            pago.setMedioPago(pagoRequest.getMedioPago());
            pago.setUrlVoucher(pagoRequest.getUrlVoucher());
            pago.setEstado(EstadoPago.VALIDADO);
            pago.setPagadoPor(PagadoPor.CONTADOR);
            pago.setComentarioContador(pagoRequest.getComentarioContador());

            Pago pagoGuardado = pagoRepository.save(pago);

            obligacion.setEstado(EstadoObligacion.PAGADA);
            obligacionRepository.save(obligacion);

            return pagoMapper.toPagoResponse(pagoGuardado);

        } catch (DataIntegrityViolationException ex) {
            throw new RuntimeException("Error de integridad al registrar el pago o actualizar la obligación.", ex);
        } catch (Exception ex) {
            throw new RuntimeException("Error inesperado al registrar el pago del contador.", ex);
        }
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

    @Override
    public List<PagoResponse> buscarMisPagos(Usuario usuario, LocalDate periodoTributario, BigDecimal monto, String orden) {
        Cliente cliente = clienteRepository.findByUsuarioId(usuario.getId())
                .orElseThrow(ContadorNotFoundException::new);

        List<Pago> pagos = pagoRepository.findAllByObligacion_Cliente_Id(cliente.getId());

        Stream<Pago> stream = pagos.stream();
        if (periodoTributario != null) {
            stream = stream.filter(p -> p.getObligacion().getPeriodoTributario().isEqual(periodoTributario));
        }
        if (monto != null) {
            stream = stream.filter(p -> p.getMontoPagado().compareTo(monto) == 0);
        }

        Comparator<Pago> comparator = Comparator.comparing(p -> p.getObligacion().getPeriodoTributario());
        if ("DESC".equalsIgnoreCase(orden)) {
            comparator = comparator.reversed();
        }

        return stream.sorted(comparator)
                .map(pagoMapper::toPagoResponse)
                .collect(Collectors.toList());
    }

    @Override
    public PagoResponse actualizarEstado(Long idPago, String nuevoEstado) {
        Pago pago = pagoRepository.findById(idPago)
                .orElseThrow(() -> new PagoNotFoundException("Pago no encontrado con ID: " + idPago));

        Optional<Obligacion> obligacion = obligacionRepository.findById(pago.getObligacion().getId());
        Obligacion obligacionActual = obligacion
                .orElseThrow(() -> new ObligacionNotFoundException("Obligación no encontrada con ID: " + pago.getObligacion().getId()));

        try {
            EstadoPago estadoEnum = EstadoPago.valueOf(nuevoEstado.toUpperCase());
            if (estadoEnum == EstadoPago.VALIDADO) {
                pago.setEstado(estadoEnum);
                obligacionActual.setEstado(EstadoObligacion.PAGADA);
            } else {
                System.out.println("Hola");
                // if (estadoEnum == EstadoPago.RECHAZADO)
            }
            Obligacion obligacionGuardado = obligacionRepository.save(obligacionActual);
            Pago pagoActualizado = pagoRepository.save(pago);
            return pagoMapper.toPagoResponse(pagoActualizado);

        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Estado de pago no válido: " + nuevoEstado);
        }
    }

    @Override
    public List<PagoResponse> buscarUltimosPagosDeMisClientes(Usuario usuario, Integer mes, String ordenMonto, String medioPago) {
        Contador contador = contadorRepository.findByUsuarioId(usuario.getId())
                .orElseThrow(ContadorNotFoundException::new);

        List<Pago> todosLosPagos = pagoRepository.findAllByContadorId(contador.getId());

        Map<Long, Pago> ultimosPagosPorCliente = todosLosPagos.stream()
                .collect(Collectors.toMap(
                        p -> p.getObligacion().getCliente().getId(),
                        p -> p,
                        (p1, p2) -> p1.getObligacion().getPeriodoTributario().isAfter(p2.getObligacion().getPeriodoTributario()) ? p1 : p2
                ));

        Stream<Pago> stream = ultimosPagosPorCliente.values().stream();

        if (mes != null) {
            stream = stream.filter(p -> p.getObligacion().getPeriodoTributario().getMonthValue() == mes);
        }

        if (medioPago != null && !medioPago.isBlank()) {
            try {
                MedioPago medioPagoEnum = MedioPago.valueOf(medioPago.toUpperCase());
                stream = stream.filter(p -> p.getMedioPago() == medioPagoEnum);
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Medio de pago no válido: " + medioPago);
            }
        }

        Comparator<Pago> comparator = Comparator.comparing(p -> p.getObligacion().getPeriodoTributario());
        if (ordenMonto != null) {
            Comparator<Pago> montoComparator = Comparator.comparing(Pago::getMontoPagado);
            if ("DESC".equalsIgnoreCase(ordenMonto)) {
                montoComparator = montoComparator.reversed();
            }
            comparator = montoComparator;
        }

        return stream.sorted(comparator)
                .map(pagoMapper::toPagoResponse)
                .collect(Collectors.toList());
    }
}