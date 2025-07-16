package com.asesoria.contable.app_ac.service;

import com.asesoria.contable.app_ac.exceptions.ClienteNotFoundException;
import com.asesoria.contable.app_ac.exceptions.DeclaracionNotFoundException;
import com.asesoria.contable.app_ac.exceptions.ObligacionNotFoundException;
import com.asesoria.contable.app_ac.mapper.DeclaracionMapper;
import com.asesoria.contable.app_ac.mapper.ObligacionMapper;
import com.asesoria.contable.app_ac.model.dto.DeclaracionRequest;
import com.asesoria.contable.app_ac.model.dto.ObligacionRequest;
import com.asesoria.contable.app_ac.model.dto.ObligacionResponse;
import com.asesoria.contable.app_ac.model.entity.*;
import com.asesoria.contable.app_ac.repository.ClienteRepository;
import com.asesoria.contable.app_ac.repository.DeclaracionRepository;
import com.asesoria.contable.app_ac.repository.ContadorRepository;
import com.asesoria.contable.app_ac.repository.ObligacionRepository;
import com.asesoria.contable.app_ac.utils.enums.EstadoObligacion;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class ObligacionServiceImpl implements ObligacionService {

    private final ObligacionRepository obligacionRepository;
    private final ObligacionMapper obligacionMapper;
    private final ClienteRepository clienteRepository;
    private final DeclaracionRepository declaracionRepository;
    private final ContadorRepository contadorRepository;
    private final ClienteService clienteService;
    private final DeclaracionMapper declaracionMapper;

    @Override
    public List<ObligacionResponse> findAll() {
        return obligacionRepository.findAll().stream()
                .map(obligacionMapper::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public ObligacionResponse findById(Long id) {
        Obligacion obligacion = obligacionRepository.findById(id)
                .orElseThrow(() -> new ObligacionNotFoundException("Obligacion no encontrada con id: " + id));
        return obligacionMapper.convertToResponse(obligacion);
    }

    @Override
    public ObligacionResponse save(ObligacionRequest obligacionRequest) {
        Cliente cliente = clienteRepository.findById(obligacionRequest.getIdCliente())
                .orElseThrow(ClienteNotFoundException::new);

        Declaracion declaracion = null;
        if (obligacionRequest.getIdDeclaracion() != null) {
            declaracion = declaracionRepository.findById(obligacionRequest.getIdDeclaracion())
                    .orElseThrow(DeclaracionNotFoundException::new);
        }

        Obligacion obligacion = obligacionMapper.convertToEntity(obligacionRequest);
        obligacion.setCliente(cliente);
        obligacion.setDeclaracion(declaracion);

        return obligacionMapper.convertToResponse(obligacionRepository.save(obligacion));
    }

    @Override
    public ObligacionResponse update(Long id, ObligacionRequest obligacionRequest) {
        Obligacion obligacion = obligacionRepository.findById(id)
                .orElseThrow(() -> new ObligacionNotFoundException("Obligacion no encontrada con id: " + id));

        Cliente cliente = clienteRepository.findById(obligacionRequest.getIdCliente())
                .orElseThrow(ClienteNotFoundException::new);

        Declaracion declaracion = null;
        if (obligacionRequest.getIdDeclaracion() != null) {
            declaracion = declaracionRepository.findById(obligacionRequest.getIdDeclaracion())
                    .orElseThrow(DeclaracionNotFoundException::new);
        }

        obligacionMapper.updateEntity(obligacionRequest, obligacion);
        obligacion.setCliente(cliente);
        obligacion.setDeclaracion(declaracion);

        return obligacionMapper.convertToResponse(obligacionRepository.save(obligacion));
    }

    @Override
    public void deleteById(Long id) {
        if (!obligacionRepository.existsById(id)) {
            throw new ObligacionNotFoundException("Obligacion no encontrada con id: " + id);
        }
        obligacionRepository.deleteById(id);
    }

    @Override
    public List<ObligacionResponse> findByClienteId(Long clienteId) {
        if (!clienteRepository.existsById(clienteId)) {
            throw new ClienteNotFoundException();
        }
        return obligacionRepository.findByClienteId(clienteId).stream()
                .map(obligacionMapper::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<ObligacionResponse> buscarObligaciones(
            Usuario usuario,
            String estado,
            String nombreCliente,
            LocalDate desde,
            LocalDate hasta,
            String orden
    ) {
        Contador contador = contadorRepository.findByUsuarioId(usuario.getId())
                .orElseThrow(() -> new RuntimeException("Contador no encontrado."));

        List<Cliente> clientes = clienteRepository.findAllByContadorId(contador.getId());

        if (nombreCliente != null && !nombreCliente.isBlank()) {
            String filtro = nombreCliente.trim().toLowerCase();
            clientes = clientes.stream()
                    .filter(c -> c.getNombres().toLowerCase().contains(filtro))
                    .toList();
        }

        List<Long> clienteIds = clientes.stream()
                .map(Cliente::getId)
                .toList();

        if (clienteIds.isEmpty()) return List.of();

        List<Obligacion> lista = obligacionRepository.findUltimaPorCliente(clienteIds);

        // ✅ Aplicar filtros en Java
        Stream<Obligacion> stream = lista.stream();

        if (estado != null && !estado.isBlank()) {
            stream = stream.filter(o -> o.getEstado() != null && o.getEstado().name().equalsIgnoreCase(estado));
        }

        if (desde != null) {
            stream = stream.filter(o -> !o.getPeriodoTributario().isBefore(desde));
        }

        if (hasta != null) {
            stream = stream.filter(o -> !o.getPeriodoTributario().isAfter(hasta));
        }

        if ("ASC".equalsIgnoreCase(orden)) {
            stream = stream.sorted(Comparator.comparing(Obligacion::getPeriodoTributario));
        } else {
            stream = stream.sorted(Comparator.comparing(Obligacion::getPeriodoTributario).reversed());
        }

        return stream
                .map(obligacionMapper::convertToResponse)
                .toList();
    }

    @Override
    public ObligacionResponse saveFromDeclaracion(DeclaracionRequest declaracionRequest) {
        Cliente cliente = clienteRepository.findById(declaracionRequest.getIdCliente())
                .orElseThrow(ClienteNotFoundException::new);

        Obligacion obligacion = new Obligacion();
        obligacion.setCliente(cliente);
        obligacion.setDeclaracion(declaracionMapper.toDeclaracion(declaracionRequest));
        obligacion.setTipo(declaracionRequest.getTipo());
        obligacion.setPeriodoTributario(declaracionRequest.getPeriodoTributario());
        obligacion.setMonto(declaracionRequest.getTotalPagarDeclaracion());
        obligacion.setFechaLimite(declaracionRequest.getFechaLimite());
        obligacion.setEstado(EstadoObligacion.PENDIENTE);

        return obligacionMapper.convertToResponse(obligacionRepository.save(obligacion));
    }

    @Override
    public List<ObligacionResponse> buscarMisObligaciones(
            Usuario usuario,
            Integer mes,
            Integer anio,
            java.math.BigDecimal montoMaximo,
            String ordenFechaLimite
    ) {
        Cliente cliente = clienteService.findEntityByUsuarioId(usuario.getId());
        List<Obligacion> obligaciones = obligacionRepository.findByClienteId(cliente.getId());

        Stream<Obligacion> stream = obligaciones.stream();

        if (mes != null) {
            stream = stream.filter(o -> o.getPeriodoTributario().getMonthValue() == mes);
        }

        if (anio != null) {
            stream = stream.filter(o -> o.getPeriodoTributario().getYear() == anio);
        }

        if (montoMaximo != null) {
            stream = stream.filter(o -> o.getMonto().compareTo(montoMaximo) <= 0);
        }

        Comparator<Obligacion> comparator = Comparator.comparing(Obligacion::getFechaLimite);
        if ("DESC".equalsIgnoreCase(ordenFechaLimite)) {
            comparator = comparator.reversed();
        }

        return stream.sorted(comparator)
                .map(obligacionMapper::convertToResponse)
                .collect(Collectors.toList());
    }
}

