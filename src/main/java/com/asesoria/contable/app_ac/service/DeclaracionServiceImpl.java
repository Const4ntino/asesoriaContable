package com.asesoria.contable.app_ac.service;

import com.asesoria.contable.app_ac.exceptions.ClienteNotFoundException;
import com.asesoria.contable.app_ac.exceptions.DeclaracionNotFoundException;
import com.asesoria.contable.app_ac.mapper.DeclaracionMapper;
import com.asesoria.contable.app_ac.model.dto.AlertaContadorRequest;
import com.asesoria.contable.app_ac.model.dto.DeclaracionRequest;
import com.asesoria.contable.app_ac.model.dto.DeclaracionResponse;
import com.asesoria.contable.app_ac.model.dto.PeriodoVencimientoResponse;
import com.asesoria.contable.app_ac.model.entity.*;
import com.asesoria.contable.app_ac.repository.ClienteRepository;
import com.asesoria.contable.app_ac.repository.ContadorRepository;
import com.asesoria.contable.app_ac.repository.DeclaracionRepository;
import com.asesoria.contable.app_ac.repository.UsuarioRepository;
import com.asesoria.contable.app_ac.utils.enums.CronogramaVencimientoSunat;
import com.asesoria.contable.app_ac.utils.enums.DeclaracionEstado;
import com.asesoria.contable.app_ac.utils.enums.EstadoCliente;
import com.asesoria.contable.app_ac.utils.enums.EstadoContador;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DeclaracionServiceImpl implements DeclaracionService {

    private final DeclaracionRepository declaracionRepository;
    private final ClienteRepository clienteRepository;
    private final DeclaracionMapper declaracionMapper;
    private final ClienteService clienteService;
    private final UsuarioRepository usuarioRepository;
    private final ContadorRepository contadorRepository;
    private final AlertaContadorService alertaContadorService;

    @Override
    public DeclaracionResponse findById(Long id) {
        return declaracionRepository.findById(id)
                .map(declaracionMapper::toDeclaracionResponse)
                .orElseThrow(DeclaracionNotFoundException::new);
    }

    @Override
    public List<DeclaracionResponse> findAll() {
        return declaracionRepository.findAll()
                .stream()
                .map(declaracionMapper::toDeclaracionResponse)
                .collect(Collectors.toList());
    }

    @Override
    public DeclaracionResponse save(DeclaracionRequest request) {
        Cliente cliente = clienteRepository.findById(request.getIdCliente())
                .orElseThrow(ClienteNotFoundException::new);

        Declaracion declaracion = declaracionMapper.toDeclaracion(request);
        declaracion.setCliente(cliente);

        Declaracion declaracionGuardada = declaracionRepository.save(declaracion);
        return declaracionMapper.toDeclaracionResponse(declaracionGuardada);
    }

    @Override
    public DeclaracionResponse update(Long id, DeclaracionRequest request) {
        Declaracion declaracion = declaracionRepository.findById(id)
                .orElseThrow(DeclaracionNotFoundException::new);

        Cliente cliente = clienteRepository.findById(request.getIdCliente())
                .orElseThrow(ClienteNotFoundException::new);

        declaracionMapper.updateDeclaracionFromRequest(request, declaracion);
        declaracion.setCliente(cliente);

        Declaracion declaracionActualizada = declaracionRepository.save(declaracion);
        return declaracionMapper.toDeclaracionResponse(declaracionActualizada);
    }

    @Override
    public void deleteById(Long id) {
        if (declaracionRepository.findById(id).isEmpty()) {
            throw new DeclaracionNotFoundException();
        }
        declaracionRepository.deleteById(id);
    }

    @Override
    public List<DeclaracionResponse> findByClienteId(Long clienteId) {
        return declaracionRepository.findByClienteId(clienteId)
                .stream()
                .map(declaracionMapper::toDeclaracionResponse)
                .collect(Collectors.toList());
    }

    @Override
    public DeclaracionResponse generarDeclaracionSiNoExiste(Usuario usuario) {
        Cliente cliente = clienteService.findEntityByUsuarioId(usuario.getId());
        String ruc = cliente.getRucDni();

        YearMonth periodo = YearMonth.now().minusMonths(1);
        LocalDate periodoTributario = periodo.atDay(1);

        // Verificar si ya existe una declaración con estado CREADO
        Optional<Declaracion> declaracionExistente =
                declaracionRepository.findByClienteIdAndPeriodoTributarioAndEstado(
                        cliente.getId(),
                        periodoTributario,
                        DeclaracionEstado.CREADO
                );

        if (declaracionExistente.isPresent()) {
            return declaracionMapper.toDeclaracionResponse(declaracionExistente.get());
        }

        int ultimoDigito = Character.getNumericValue(ruc.charAt(ruc.length() - 1));
        LocalDate fechaLimite = CronogramaVencimientoSunat.getFechaVencimiento(periodo.toString(), ultimoDigito);

        Declaracion nuevaDeclaracion = new Declaracion();
        nuevaDeclaracion.setCliente(cliente);
        nuevaDeclaracion.setPeriodoTributario(periodoTributario);
        nuevaDeclaracion.setTipo("IGV");
        nuevaDeclaracion.setFechaLimite(fechaLimite);
        nuevaDeclaracion.setEstadoCliente(EstadoCliente.PENDIENTE);
        nuevaDeclaracion.setEstadoContador(EstadoContador.PENDIENTE);
        nuevaDeclaracion.setEstado(DeclaracionEstado.CREADO);

        Declaracion declaracionGuardada = declaracionRepository.save(nuevaDeclaracion);
        return declaracionMapper.toDeclaracionResponse(declaracionGuardada);
    }

    @Override
    public List<DeclaracionResponse> buscarMisDeclaraciones(Usuario usuario, LocalDate fechaInicio, LocalDate fechaFin, DeclaracionEstado estado, EstadoContador estadoContador) {
        Cliente cliente = clienteService.findEntityByUsuarioId(usuario.getId());

        Specification<Declaracion> spec = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(criteriaBuilder.equal(root.get("cliente"), cliente));

            if (fechaInicio != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("periodoTributario"), fechaInicio));
            }
            if (fechaFin != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("periodoTributario"), fechaFin));
            }
            if (estado != null) {
                predicates.add(criteriaBuilder.equal(root.get("estado"), estado));
            }
            if (estadoContador != null) {
                predicates.add(criteriaBuilder.equal(root.get("estadoContador"), estadoContador));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };

        return declaracionRepository.findAll(spec).stream()
                .map(declaracionMapper::toDeclaracionResponse)
                .collect(Collectors.toList());
    }

    @Override
    public DeclaracionResponse notificarContador(Long declaracionId, Usuario usuario) {
        Declaracion declaracion = declaracionRepository.findById(declaracionId)
                .orElseThrow(DeclaracionNotFoundException::new);

        if (!declaracion.getCliente().getUsuario().getId().equals(usuario.getId())) {
            throw new AccessDeniedException("No tiene permiso para notificar sobre esta declaración.");
        }

        declaracion.setEstado(DeclaracionEstado.CONTADOR_NOTIFICADO);

        // Crear alerta para el contador
        if (declaracion.getCliente().getContador() != null) {
            Contador contador = declaracion.getCliente().getContador();
            AlertaContadorRequest alertaRequest = new AlertaContadorRequest();
            alertaRequest.setIdContador(contador.getId());
            alertaRequest.setDescripcion("El cliente " + declaracion.getCliente().getNombres() + " " + declaracion.getCliente().getApellidos() + " ha notificado que la declaración del período " + declaracion.getPeriodoTributario().getMonth().name() + "-" + declaracion.getPeriodoTributario().getYear() + " se aproxima a su fecha de vencimiento.");
            alertaRequest.setFechaExpiracion(LocalDateTime.now().plusDays(10));
            // El estado se establece por defecto en ACTIVO en el @PrePersist de AlertaContador
            alertaContadorService.save(alertaRequest);
        }

        Declaracion declaracionActualizada = declaracionRepository.save(declaracion);
        return declaracionMapper.toDeclaracionResponse(declaracionActualizada);
    }

    @Override
    public DeclaracionResponse findFirstCreadaByUsuario(Usuario usuario) {
        Cliente cliente = clienteService.findEntityByUsuarioId(usuario.getId());

        return declaracionRepository.findFirstByClienteIdAndEstadoOrderByPeriodoTributarioAsc(cliente.getId(), DeclaracionEstado.CREADO)
                .map(declaracionMapper::toDeclaracionResponse)
                .orElseThrow(() -> new DeclaracionNotFoundException());
    }

    @Override
    public PeriodoVencimientoResponse getPeriodoActualYFechaVencimiento(Usuario usuario) {
        Cliente cliente = clienteService.findEntityByUsuarioId(usuario.getId());
        String ruc = cliente.getRucDni();

        YearMonth periodoActual = YearMonth.now();
        int ultimoDigito = Character.getNumericValue(ruc.charAt(ruc.length() - 1));
        LocalDate fechaLimite = CronogramaVencimientoSunat.getFechaVencimiento(periodoActual.toString(), ultimoDigito);

        return new PeriodoVencimientoResponse(periodoActual, fechaLimite);
    }
}
