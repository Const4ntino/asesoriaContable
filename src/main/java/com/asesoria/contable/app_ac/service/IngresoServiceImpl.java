package com.asesoria.contable.app_ac.service;

import com.asesoria.contable.app_ac.exceptions.ClienteNotFoundException;
import com.asesoria.contable.app_ac.exceptions.IngresoNotFoundException;
import com.asesoria.contable.app_ac.mapper.IngresoMapper;
import com.asesoria.contable.app_ac.model.dto.IngresoRequest;
import com.asesoria.contable.app_ac.model.dto.IngresoResponse;
import com.asesoria.contable.app_ac.model.entity.Cliente;
import com.asesoria.contable.app_ac.model.entity.Ingreso;
import com.asesoria.contable.app_ac.model.entity.Usuario;
import com.asesoria.contable.app_ac.repository.ClienteRepository;
import com.asesoria.contable.app_ac.repository.IngresoRepository;
import com.asesoria.contable.app_ac.specification.IngresoSpecification;
import com.asesoria.contable.app_ac.utils.enums.Regimen;
import com.asesoria.contable.app_ac.utils.enums.TipoTributario;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class IngresoServiceImpl implements IngresoService {

    private final IngresoRepository ingresoRepository;
    private final ClienteRepository clienteRepository;
    private final IngresoMapper ingresoMapper;

    private static final BigDecimal IGV_RATE = new BigDecimal("0.18");

    @Override
    public IngresoResponse findById(Long id) {
        return ingresoRepository.findById(id)
                .map(ingresoMapper::toIngresoResponse)
                .orElseThrow(IngresoNotFoundException::new);
    }

    @Override
    public List<IngresoResponse> findAll() {
        return ingresoRepository.findAll()
                .stream()
                .map(ingresoMapper::toIngresoResponse)
                .collect(Collectors.toList());
    }

    @Override
    public IngresoResponse save(IngresoRequest request) {
        Cliente cliente = clienteRepository.findById(request.getIdCliente())
                .orElseThrow(ClienteNotFoundException::new);

        Ingreso ingreso = ingresoMapper.toIngreso(request);
        ingreso.setCliente(cliente);

        // Lógica para calcular IGV
        if (request.getTipoTributario() == TipoTributario.GRAVADA &&
                cliente.getRegimen() != Regimen.NRUS) {

            ingreso.setMontoIgv(request.getMonto().multiply(IGV_RATE));
        } else {
            ingreso.setMontoIgv(BigDecimal.ZERO);
        }

        Ingreso ingresoGuardado = ingresoRepository.save(ingreso);
        return ingresoMapper.toIngresoResponse(ingresoGuardado);
    }

    @Override
    public IngresoResponse update(Long id, IngresoRequest request) {
        Ingreso ingreso = ingresoRepository.findById(id)
                .orElseThrow(IngresoNotFoundException::new);

        Cliente cliente = clienteRepository.findById(request.getIdCliente())
                .orElseThrow(ClienteNotFoundException::new);

        ingresoMapper.updateIngresoFromRequest(request, ingreso);
        ingreso.setCliente(cliente);

        // Lógica para calcular el IGV solo si es GRAVADA y el cliente NO es NRUS
        if (request.getTipoTributario() == TipoTributario.GRAVADA &&
                cliente.getRegimen() != Regimen.NRUS) {

            ingreso.setMontoIgv(request.getMonto().multiply(IGV_RATE));
        } else {
            ingreso.setMontoIgv(BigDecimal.ZERO);
        }

        Ingreso ingresoActualizado = ingresoRepository.save(ingreso);
        return ingresoMapper.toIngresoResponse(ingresoActualizado);
    }

    @Override
    public void deleteById(Long id) {
        if (ingresoRepository.findById(id).isEmpty()) {
            throw new IngresoNotFoundException();
        }
        ingresoRepository.deleteById(id);
    }

    @Override
    public IngresoResponse saveByUsuario(IngresoRequest request, Usuario usuario) {
        Cliente cliente = clienteRepository.findByUsuarioId(usuario.getId())
                .orElseThrow(ClienteNotFoundException::new);

        Ingreso ingreso = ingresoMapper.toIngreso(request);
        ingreso.setCliente(cliente);

        if (request.getTipoTributario() == TipoTributario.GRAVADA && cliente.getRegimen() != Regimen.NRUS) {
            ingreso.setMontoIgv(request.getMonto().multiply(IGV_RATE));
        } else {
            ingreso.setMontoIgv(BigDecimal.ZERO);
        }

        return ingresoMapper.toIngresoResponse(ingresoRepository.save(ingreso));
    }

    @Override
    public IngresoResponse updateMyIngreso(Long id, IngresoRequest request, Usuario usuario) {
        Cliente cliente = clienteRepository.findByUsuarioId(usuario.getId())
                .orElseThrow(ClienteNotFoundException::new);

        Ingreso ingreso = ingresoRepository.findById(id)
                .orElseThrow(IngresoNotFoundException::new);

        if (!ingreso.getCliente().getId().equals(cliente.getId())) {
            throw new AccessDeniedException("No puedes modificar este ingreso.");
        }

        ingresoMapper.updateIngresoFromRequest(request, ingreso);
        ingreso.setCliente(cliente);

        if (request.getTipoTributario() == TipoTributario.GRAVADA && cliente.getRegimen() != Regimen.NRUS) {
            ingreso.setMontoIgv(request.getMonto().multiply(IGV_RATE));
        } else {
            ingreso.setMontoIgv(BigDecimal.ZERO);
        }

        return ingresoMapper.toIngresoResponse(ingresoRepository.save(ingreso));
    }

    @Override
    public void deleteMyIngreso(Long id, Usuario usuario) {
        Cliente cliente = clienteRepository.findByUsuarioId(usuario.getId())
                .orElseThrow(ClienteNotFoundException::new);

        Ingreso ingreso = ingresoRepository.findById(id)
                .orElseThrow(IngresoNotFoundException::new);

        if (!ingreso.getCliente().getId().equals(cliente.getId())) {
            throw new AccessDeniedException("No puedes eliminar este ingreso.");
        }

        ingresoRepository.deleteById(id);
    }

    @Override
    public List<IngresoResponse> findByClienteId(Long clienteId) {
        return ingresoRepository.findByClienteId(clienteId)
                .stream()
                .map(ingresoMapper::toIngresoResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<IngresoResponse> findByClienteIdAndFechaBetween(Long clienteId, LocalDate startDate, LocalDate endDate) {
        return ingresoRepository.findByClienteIdAndFechaBetween(clienteId, startDate, endDate)
                .stream()
                .map(ingresoMapper::toIngresoResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<IngresoResponse> findByUsuarioId(Long usuarioId) {
        Cliente cliente = clienteRepository.findByUsuarioId(usuarioId)
                .orElseThrow(ClienteNotFoundException::new);
        return ingresoRepository.findByClienteId(cliente.getId())
                .stream()
                .map(ingresoMapper::toIngresoResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<IngresoResponse> findByUsuarioIdAndPeriodo(Long usuarioId, Integer mes, Integer anio) {
        Cliente cliente = clienteRepository.findByUsuarioId(usuarioId)
                .orElseThrow(ClienteNotFoundException::new);
        
        // Crear la especificación con los filtros de mes y año
        var specification = IngresoSpecification.filtrarIngresos(
                cliente.getId(),
                null,  // montoMinimo
                null,  // montoMaximo
                null,  // fechaInicio
                null,  // fechaFin
                mes,   // mes
                anio,  // anio
                null,  // tipoTributario
                null,  // descripcion
                null   // nroComprobante
        );
        
        // Ejecutar la consulta con la especificación
        return ingresoRepository.findAll(specification)
                .stream()
                .map(ingresoMapper::toIngresoResponse)
                .collect(Collectors.toList());
    }

    @Override
    public BigDecimal calcularTotalMesActual(Long clienteId) {
        YearMonth mesActual = YearMonth.now();
        LocalDate inicioMes = mesActual.atDay(1);
        LocalDate finMes = mesActual.atEndOfMonth();
        return ingresoRepository.sumMontoByClienteIdAndFechaBetween(clienteId, inicioMes, finMes);
    }

    @Override
    public BigDecimal calcularTotalMesAnterior(Long clienteId) {
        YearMonth mesAnterior = YearMonth.now().minusMonths(1);
        LocalDate inicioMes = mesAnterior.atDay(1);
        LocalDate finMes = mesAnterior.atEndOfMonth();
        return ingresoRepository.sumMontoByClienteIdAndFechaBetween(clienteId, inicioMes, finMes);
    }

    @Override
    public Map<String, BigDecimal> obtenerIngresosPorCategoria(Long clienteId) {
        return ingresoRepository.sumMontoByCategoria(clienteId).stream()
                .collect(Collectors.toMap(
                        map -> (String) map.get("categoria"),
                        map -> (BigDecimal) map.get("total")
                ));
    }

    @Override
    public Long contarComprobantesMesActual(Long clienteId) {
        YearMonth mesActual = YearMonth.now();
        LocalDate inicioMes = mesActual.atDay(1);
        LocalDate finMes = mesActual.atEndOfMonth();
        return ingresoRepository.countByClienteIdAndFechaBetween(clienteId, inicioMes, finMes);
    }

    @Override
    public Map<String, BigDecimal> obtenerIngresosPorTipoTributario(Long clienteId) {
        return ingresoRepository.sumRawByTipoTributario(clienteId).stream()
                .collect(Collectors.toMap(
                        r -> ((TipoTributario) r[0]).name(),
                        r -> (BigDecimal) r[1]
                ));
    }

    @Override
    public List<Map<String, Object>> identificarIngresosRecurrentes(Long clienteId) {
        return ingresoRepository.findIngresosRecurrentes(clienteId);
    }

    @Override
    public BigDecimal getSumaIngresosGravadosMesAnterior(Long clienteId) {
        YearMonth mesAnterior = YearMonth.now().minusMonths(1);
        LocalDate inicioMes = mesAnterior.atDay(1);
        LocalDate finMes = mesAnterior.atEndOfMonth();
        return ingresoRepository.sumMontoByClienteIdAndTipoTributarioAndFechaBetween(clienteId, TipoTributario.GRAVADA, inicioMes, finMes);
    }

    @Override
    public BigDecimal getSumaIngresosExoneradosMesAnterior(Long clienteId) {
        YearMonth mesAnterior = YearMonth.now().minusMonths(1);
        LocalDate inicioMes = mesAnterior.atDay(1);
        LocalDate finMes = mesAnterior.atEndOfMonth();
        return ingresoRepository.sumMontoByClienteIdAndTipoTributarioAndFechaBetween(clienteId, TipoTributario.EXONERADA, inicioMes, finMes);
    }

    @Override
    public BigDecimal getSumaIngresosInafectosMesAnterior(Long clienteId) {
        YearMonth mesAnterior = YearMonth.now().minusMonths(1);
        LocalDate inicioMes = mesAnterior.atDay(1);
        LocalDate finMes = mesAnterior.atEndOfMonth();
        return ingresoRepository.sumMontoByClienteIdAndTipoTributarioAndFechaBetween(clienteId, TipoTributario.INAFECTA, inicioMes, finMes);
    }

    @Override
    public BigDecimal getSumaIgvIngresosMesAnterior(Long clienteId) {
        YearMonth mesAnterior = YearMonth.now().minusMonths(1);
        LocalDate inicioMes = mesAnterior.atDay(1);
        LocalDate finMes = mesAnterior.atEndOfMonth();
        return ingresoRepository.sumMontoIgvByClienteIdAndFechaBetween(clienteId, inicioMes, finMes);
    }
    
    @Override
    public Page<IngresoResponse> filtrarIngresos(
            Long clienteId,
            BigDecimal montoMinimo,
            BigDecimal montoMaximo,
            LocalDate fechaInicio,
            LocalDate fechaFin,
            Integer mes,
            Integer anio,
            TipoTributario tipoTributario,
            String descripcion,
            String nroComprobante,
            Pageable pageable) {
        
        // Verificar que el cliente existe
        if (clienteId != null) {
            clienteRepository.findById(clienteId)
                    .orElseThrow(ClienteNotFoundException::new);
        }
        
        // Crear la especificación con los filtros
        var specification = IngresoSpecification.filtrarIngresos(
                clienteId,
                montoMinimo,
                montoMaximo,
                fechaInicio,
                fechaFin,
                mes,
                anio,
                tipoTributario,
                descripcion,
                nroComprobante);
        
        // Ejecutar la consulta con la especificación y paginación
        return ingresoRepository.findAll(specification, pageable)
                .map(ingresoMapper::toIngresoResponse);
    }
}

