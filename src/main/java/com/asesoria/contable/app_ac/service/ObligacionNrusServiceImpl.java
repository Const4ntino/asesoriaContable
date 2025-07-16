package com.asesoria.contable.app_ac.service;

import com.asesoria.contable.app_ac.exceptions.ObligacionNrusNotFoundException;
import com.asesoria.contable.app_ac.mapper.ObligacionNrusMapper;
import com.asesoria.contable.app_ac.model.dto.ObligacionNrusRequest;
import com.asesoria.contable.app_ac.model.dto.ObligacionNrusResponse;
import com.asesoria.contable.app_ac.model.entity.ObligacionNrus;
import com.asesoria.contable.app_ac.model.entity.Cliente;
import com.asesoria.contable.app_ac.repository.ClienteRepository;
import com.asesoria.contable.app_ac.repository.IngresoRepository;
import com.asesoria.contable.app_ac.repository.ObligacionNrusRepository;
import com.asesoria.contable.app_ac.utils.enums.EstadoObligacion;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class ObligacionNrusServiceImpl implements ObligacionNrusService {

    private final ObligacionNrusRepository obligacionNrusRepository;
    private final ObligacionNrusMapper obligacionNrusMapper;
    private final ClienteRepository clienteRepository;
    private final IngresoRepository ingresoRepository;

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

    @Override
    public ObligacionNrusResponse generarOActualizarObligacionNrus(Long idCliente, LocalDate periodoTributario) {
        Cliente cliente = clienteRepository.findById(idCliente)
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado")); // Considerar una excepción más específica

        // Obtener el primer día y el último día del periodo tributario
        LocalDate primerDiaMes = periodoTributario.withDayOfMonth(1);
        LocalDate ultimoDiaMes = periodoTributario.withDayOfMonth(periodoTributario.lengthOfMonth());

        // Sumar los ingresos del cliente para el periodo
        BigDecimal totalIngresos = ingresoRepository.sumMontoByClienteIdAndPeriodo(idCliente, periodoTributario);

        // Determinar el monto de la cuota NRUS
        BigDecimal montoCuota;
        String observaciones = "Sin observaciones";
        EstadoObligacion estado = EstadoObligacion.PENDIENTE;

        if (totalIngresos.compareTo(BigDecimal.valueOf(1700)) <= 0) {
            montoCuota = BigDecimal.valueOf(20);
        } else if (totalIngresos.compareTo(BigDecimal.valueOf(2500)) <= 0) {
            montoCuota = BigDecimal.valueOf(50);
        } else if (totalIngresos.compareTo(BigDecimal.valueOf(3500)) <= 0) {
            montoCuota = BigDecimal.valueOf(100);
        } else if (totalIngresos.compareTo(BigDecimal.valueOf(5000)) <= 0) {
            montoCuota = BigDecimal.valueOf(150);
        } else {
            montoCuota = BigDecimal.ZERO; // O algún valor que indique excedido
            observaciones = "Exceso detectado – No puede pagar cuota fija.";
            estado = EstadoObligacion.NO_DISPONIBLE;
        }

        // Calcular la fecha límite (5 del mes siguiente al periodo tributario)
        LocalDate fechaLimite = periodoTributario.plusMonths(1).withDayOfMonth(5);

        // Buscar si ya existe una obligación NRUS para este cliente y periodo
        Optional<ObligacionNrus> existingObligacion = obligacionNrusRepository.findByClienteAndPeriodoTributario(cliente, periodoTributario);

        ObligacionNrus obligacionNrus;
        if (existingObligacion.isPresent()) {
            obligacionNrus = existingObligacion.get();
            // Actualizar campos si ya existe
            obligacionNrus.setMonto(montoCuota);
            obligacionNrus.setFechaLimite(fechaLimite);
            obligacionNrus.setEstado(estado);
            obligacionNrus.setObservaciones(observaciones);
            obligacionNrus.setFechaActualizacion(LocalDateTime.now());
        } else {
            // Crear nueva obligación si no existe
            obligacionNrus = new ObligacionNrus();
            obligacionNrus.setCliente(cliente);
            obligacionNrus.setPeriodoTributario(periodoTributario);
            obligacionNrus.setTipo("CUOTA MENSUAL"); // Tipo fijo para NRUS
            obligacionNrus.setMonto(montoCuota);
            obligacionNrus.setFechaLimite(fechaLimite);
            obligacionNrus.setEstado(estado);
            obligacionNrus.setObservaciones(observaciones);
            obligacionNrus.setFechaCreacion(LocalDateTime.now());
        }

        return obligacionNrusMapper.convertToDto(obligacionNrusRepository.save(obligacionNrus));
    }

    @Override
    public List<ObligacionNrusResponse> getObligacionesByClienteId(Long idCliente, LocalDate periodo, EstadoObligacion estado) {
        return obligacionNrusRepository.findByClienteIdAndOptionalPeriodoAndEstado(idCliente, periodo, estado).stream()
                .map(obligacionNrusMapper::convertToDto)
                .collect(Collectors.toList());
    }
}
