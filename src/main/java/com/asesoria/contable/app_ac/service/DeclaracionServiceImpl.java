package com.asesoria.contable.app_ac.service;

import com.asesoria.contable.app_ac.exceptions.ClienteNotFoundException;
import com.asesoria.contable.app_ac.exceptions.DeclaracionNotFoundException;
import com.asesoria.contable.app_ac.mapper.DeclaracionMapper;
import com.asesoria.contable.app_ac.model.dto.*;
import com.asesoria.contable.app_ac.model.entity.*;
import com.asesoria.contable.app_ac.utils.enums.*;
import com.asesoria.contable.app_ac.repository.ClienteRepository;
import com.asesoria.contable.app_ac.repository.ContadorRepository;
import com.asesoria.contable.app_ac.repository.DeclaracionRepository;
import com.asesoria.contable.app_ac.repository.UsuarioRepository;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;
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
    private final ObligacionService obligacionService;
    private final AuthService authService;
    private final BitacoraService bitacoraService;
    private final AlertaClienteService alertaClienteService;

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

        // Verificar si ya existe UNA declaración para ese cliente y periodo, sin importar el estado
        Optional<Declaracion> declaracionExistente =
                declaracionRepository.findByClienteIdAndPeriodoTributario(
                        cliente.getId(),
                        periodoTributario
                );

        if (declaracionExistente.isPresent()) {
            return declaracionMapper.toDeclaracionResponse(declaracionExistente.get());
        }

        int ultimoDigito = Character.getNumericValue(ruc.charAt(ruc.length() - 1));
        LocalDate fechaLimite = CronogramaVencimientoSunat.getFechaVencimiento(periodo.toString(), ultimoDigito);

        // Determinar el estado según si ya venció o no
        DeclaracionEstado estado;
        if (LocalDate.now().isAfter(fechaLimite)) {
            estado = DeclaracionEstado.LISTO;
        } else {
            estado = DeclaracionEstado.CREADO;
        }

        Declaracion nuevaDeclaracion = new Declaracion();
        nuevaDeclaracion.setCliente(cliente);
        nuevaDeclaracion.setPeriodoTributario(periodoTributario);
        nuevaDeclaracion.setTipo("IGV +Renta");
        nuevaDeclaracion.setFechaLimite(fechaLimite);
        nuevaDeclaracion.setEstadoCliente(EstadoCliente.PENDIENTE);
        nuevaDeclaracion.setEstadoContador(EstadoContador.PENDIENTE);
        nuevaDeclaracion.setEstado(estado);
//        nuevaDeclaracion.setEstado(DeclaracionEstado.CREADO);

        Declaracion declaracionGuardada = declaracionRepository.save(nuevaDeclaracion);

        // 👉 Si está por vencer (<= 5 días pero no vencido aún), crear alerta
        long diasRestantes = ChronoUnit.DAYS.between(LocalDate.now(), fechaLimite);
        if (diasRestantes <= 5 && diasRestantes >= 0) {
            AlertaClienteRequest alerta = new AlertaClienteRequest();
            alerta.setIdCliente(cliente.getId());
            alerta.setDescripcion("La declaración del periodo " + periodo + " está por vencer en " + diasRestantes + " días.");
            alerta.setTipo(com.asesoria.contable.app_ac.utils.enums.TipoAlertaCliente.DECLARACION_POR_VENCER.name());
            alerta.setFechaExpiracion(fechaLimite.atTime(23, 59));

            alertaClienteService.save(alerta);

            AlertaContadorRequest alerta2 = new AlertaContadorRequest();
            alerta2.setIdContador(cliente.getContador().getId());
            alerta2.setDescripcion("La declaración del cliente " + cliente.getNombres() + " " + cliente.getApellidos() + " RUC: " + cliente.getRucDni() + " del periodo " + periodo + " está por vencer en " + diasRestantes + " días.");
            alerta2.setTipo(com.asesoria.contable.app_ac.utils.enums.TipoAlertaContador.DECLARACION_POR_VENCER.name());
            alerta2.setFechaExpiracion(fechaLimite.atTime(23, 59));

            alertaContadorService.save(alerta2);
        }

        // 👉 Registrar en la bitácora
        Usuario usuarioActual = authService.getUsuarioActual();
        bitacoraService.registrarMovimiento(
                usuarioActual,
                Modulo.DECLARACION,
                Accion.DECLARACION_GENERADA,
                "Se generó la declaración del periodo " + periodo + " para el cliente " + cliente.getNombres() + " " + cliente.getApellidos() + " RUC: " + cliente.getRucDni()
        );

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

        // Obtener el periodo tributario del mes anterior
        YearMonth periodoAnterior = YearMonth.now().minusMonths(1);
        LocalDate periodoTributario = periodoAnterior.atDay(1);

        return declaracionRepository.findByClienteIdAndPeriodoTributario(cliente.getId(), periodoTributario)
                .map(declaracionMapper::toDeclaracionResponse)
                .orElseThrow(DeclaracionNotFoundException::new);
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

    @Override
    public List<DeclaracionResponse> getLatestDeclarationsForMyClients(Usuario usuario) {
        Contador contador = contadorRepository.findByUsuarioId(usuario.getId())
                .orElseThrow(() -> new RuntimeException("Contador no encontrado para el usuario."));

        List<Cliente> clientes = clienteRepository.findAllByContadorId(contador.getId());
        List<Long> clienteIds = clientes.stream()
                .map(Cliente::getId)
                .collect(Collectors.toList());

        if (clienteIds.isEmpty()) {
            return new ArrayList<>();
        }

        List<Declaracion> latestDeclarations = declaracionRepository.findLatestDeclarationsForClients(clienteIds);

        return latestDeclarations.stream()
                .map(declaracionMapper::toDeclaracionResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<DeclaracionResponse> searchLatestDeclarationsForMyClients(
            Usuario usuario,
            String nombresCliente,
            String regimenCliente,
            String rucDniCliente,
            Integer periodoTributarioMes,
            java.math.BigDecimal totalPagarDeclaracion,
            DeclaracionEstado estado) {

        Contador contador = contadorRepository.findByUsuarioId(usuario.getId())
                .orElseThrow(() -> new RuntimeException("Contador no encontrado para el usuario."));

        List<Cliente> clientesDelContador = clienteRepository.findAllByContadorId(contador.getId());
        List<Long> clienteIdsDelContador = clientesDelContador.stream()
                .map(Cliente::getId)
                .collect(Collectors.toList());

        if (clienteIdsDelContador.isEmpty()) {
            return new ArrayList<>();
        }

        Specification<Declaracion> spec = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Filter by clients associated with the accountant
            predicates.add(root.get("cliente").get("id").in(clienteIdsDelContador));

            // Apply optional filters
            if (nombresCliente != null && !nombresCliente.isEmpty()) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("cliente").get("nombres")), "%" + nombresCliente.toLowerCase() + "%"));
            }
            if (regimenCliente != null && !regimenCliente.isEmpty()) {
                predicates.add(criteriaBuilder.equal(root.get("cliente").get("regimen"), regimenCliente));
            }
            if (rucDniCliente != null && !rucDniCliente.isEmpty()) {
                predicates.add(criteriaBuilder.like(root.get("cliente").get("rucDni"), "%" + rucDniCliente + "%"));
            }
            if (periodoTributarioMes != null) {
                predicates.add(criteriaBuilder.equal(criteriaBuilder.function("MONTH", Integer.class, root.get("periodoTributario")), periodoTributarioMes));
                predicates.add(criteriaBuilder.equal(criteriaBuilder.function("YEAR", Integer.class, root.get("periodoTributario")), 2025));
            }
            if (totalPagarDeclaracion != null) {
                predicates.add(criteriaBuilder.equal(root.get("totalPagarDeclaracion"), totalPagarDeclaracion));
            }
            if (estado != null) {
                predicates.add(criteriaBuilder.equal(root.get("estado"), estado));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };

        // Get all declarations for the accountant's clients that match the filters
        List<Declaracion> filteredDeclarations = declaracionRepository.findAll(spec);

        List<Declaracion> latestDeclarations = declaracionRepository.findLatestDeclarationsForClients(clienteIdsDelContador);

        // Now filter these latest declarations based on the provided criteria
        List<Declaracion> result = latestDeclarations.stream()
                .filter(declaracion -> {
                    boolean matches = true;
                    if (nombresCliente != null && !nombresCliente.isEmpty()) {
                        matches = matches && declaracion.getCliente().getNombres().toLowerCase().contains(nombresCliente.toLowerCase());
                    }
                    if (regimenCliente != null && !regimenCliente.isEmpty()) {
                        matches = matches && declaracion.getCliente().getRegimen().name().equals(regimenCliente);
                    }
                    if (rucDniCliente != null && !rucDniCliente.isEmpty()) {
                        matches = matches && declaracion.getCliente().getRucDni().contains(rucDniCliente);
                    }
                    if (periodoTributarioMes != null) {
                        matches = matches && declaracion.getPeriodoTributario().getMonthValue() == periodoTributarioMes && declaracion.getPeriodoTributario().getYear() == 2025;
                    }
                    if (totalPagarDeclaracion != null) {
                        matches = matches && declaracion.getTotalPagarDeclaracion().compareTo(totalPagarDeclaracion) == 0;
                    }
                    if (estado != null) {
                        matches = matches && declaracion.getEstado().equals(estado);
                    }
                    return matches;
                })
                .collect(Collectors.toList());

        return result.stream()
                .map(declaracionMapper::toDeclaracionResponse)
                .collect(Collectors.toList());
    }

    @Override
    public DeclaracionResponse marcarComoDeclaradoYGenerarObligacion(Long declaracionId, String observaciones) {
        Declaracion declaracion = declaracionRepository.findById(declaracionId)
                .orElseThrow(DeclaracionNotFoundException::new);

        declaracion.setEstado(DeclaracionEstado.DECLARADO);
        Declaracion declaracionActualizada = declaracionRepository.save(declaracion);

        // Crear la obligación
        ObligacionRequest obligacionRequest = new ObligacionRequest();
        obligacionRequest.setIdDeclaracion(declaracionActualizada.getId());
        obligacionRequest.setIdCliente(declaracionActualizada.getCliente().getId());
        obligacionRequest.setTipo("IGV + Renta");
        obligacionRequest.setPeriodoTributario(declaracionActualizada.getPeriodoTributario());
        obligacionRequest.setMonto(declaracionActualizada.getTotalPagarDeclaracion());
        obligacionRequest.setFechaLimite(declaracionActualizada.getFechaLimite());
        obligacionRequest.setEstado(EstadoObligacion.PENDIENTE);
        if (observaciones != null) {
            obligacionRequest.setObservaciones(observaciones);
        }

        obligacionService.save(obligacionRequest);

        // 👉 Generar alerta por nueva obligación
        AlertaClienteRequest alerta = new AlertaClienteRequest();
        alerta.setIdCliente(declaracion.getCliente().getId());
        alerta.setDescripcion("Declaración completa, ahora tienes una nueva obligación pendiente del periodo " +
                declaracion.getPeriodoTributario() +
                " por un monto de S/ " + declaracion.getTotalPagarDeclaracion());
        alerta.setTipo(com.asesoria.contable.app_ac.utils.enums.TipoAlertaCliente.NUEVA_OBLIGACION.name());
        alerta.setFechaExpiracion(declaracion.getFechaLimite().atTime(23, 59)); // Convertimos a LocalDateTime

        alertaClienteService.save(alerta);

        // 👉 Generar alerta por nueva obligación
        Cliente cliente = declaracion.getCliente();
        AlertaContadorRequest alerta2 = new AlertaContadorRequest();
        alerta2.setIdContador(declaracion.getCliente().getContador().getId());
        alerta2.setDescripcion("Declaración completa, ahora el cliente " + cliente.getNombres() + " " + cliente.getApellidos() + " RUC: " + cliente.getRucDni() + " tiene una nueva obligación pendiente del periodo " +
                declaracion.getPeriodoTributario() +
                " por un monto de S/ " + declaracion.getTotalPagarDeclaracion());
        alerta2.setTipo(com.asesoria.contable.app_ac.utils.enums.TipoAlertaContador.NUEVA_OBLIGACION.name());
        alerta2.setFechaExpiracion(declaracion.getFechaLimite().atTime(23, 59)); // Convertimos a LocalDateTime

        alertaContadorService.save(alerta2);

        return declaracionMapper.toDeclaracionResponse(declaracionActualizada);
    }

    @Override
    public DeclaracionResponse marcarComoEnProceso(Long declaracionId, String url, BigDecimal monto) {
        Declaracion declaracion = declaracionRepository.findById(declaracionId)
                .orElseThrow(DeclaracionNotFoundException::new);

        declaracion.setEstado(DeclaracionEstado.EN_PROCESO);
        declaracion.setTotalPagarDeclaracion(monto);
        declaracion.setTotalPagarDeclaracion(declaracion.getTotalPagarDeclaracion());
        if (url != null && !url.isBlank()) {
            declaracion.setUrlConstanciaDeclaracion(url);
        }

        Declaracion declaracionActualizada = declaracionRepository.save(declaracion);

        // 👉 Crear alerta de "EN PROCESO"
        AlertaClienteRequest alerta = new AlertaClienteRequest();
        alerta.setIdCliente(declaracion.getCliente().getId());
        alerta.setDescripcion("La declaración del periodo " + declaracion.getPeriodoTributario() +
                " se encuentra EN PROCESO. Monto estimado a pagar: S/ " + monto);
        alerta.setTipo(com.asesoria.contable.app_ac.utils.enums.TipoAlertaCliente.DECLARACION_EN_PROCESO.name());
        alerta.setFechaExpiracion(declaracion.getFechaLimite().atTime(23, 59));

        alertaClienteService.save(alerta);

        Cliente cliente = declaracion.getCliente();
        // 👉 Crear alerta de "EN PROCESO"
        AlertaContadorRequest alerta2 = new AlertaContadorRequest();
        alerta2.setIdContador(cliente.getContador().getId());
        alerta2.setDescripcion("Has inicializado el proceso de Declaración del cliente " + cliente.getNombres() + " " + cliente.getApellidos() + " RUC: " + cliente.getRucDni() + ", en el periodo: " + declaracion.getPeriodoTributario()  + " Monto estimado a pagar: S/ " + monto);
        alerta2.setTipo(com.asesoria.contable.app_ac.utils.enums.TipoAlertaContador.DECLARACION_EN_PROCESO.name());
        alerta2.setFechaExpiracion(declaracion.getFechaLimite().atTime(23, 59));

        alertaContadorService.save(alerta2);

        return declaracionMapper.toDeclaracionResponse(declaracionActualizada);
    }

    @Override
    public DeclaracionResponse subirUrlConstanciaDeclaracion(Long declaracionId, String urlConstancia, String tipo) {
        Declaracion declaracion = declaracionRepository.findById(declaracionId)
                .orElseThrow(DeclaracionNotFoundException::new);

        if (urlConstancia != null && !urlConstancia.isBlank()) {
            if ("DECLARACION".equals(tipo)) {
                eliminarArchivoAnterior(declaracion.getUrlConstanciaDeclaracion());
                declaracion.setUrlConstanciaDeclaracion(urlConstancia);
            } else if ("SUNAT".equals(tipo)) {
                eliminarArchivoAnterior(declaracion.getUrlConstanciaSunat());
                declaracion.setUrlConstanciaSunat(urlConstancia);
            } else {
                System.out.println("Tipo incorrecto");
            }
        }

        Declaracion declaracionActualizada = declaracionRepository.save(declaracion);
        return declaracionMapper.toDeclaracionResponse(declaracionActualizada);
    }

    public static void eliminarArchivoAnterior(String urlRelativa) {
        if (urlRelativa == null || urlRelativa.isBlank()) return;

        // Quita el prefijo inicial si lo tiene, para convertirlo en ruta real
        String rutaRelativa = urlRelativa.startsWith("/") ? urlRelativa.substring(1) : urlRelativa;

        // Ruta absoluta del archivo en el servidor
        Path rutaAbsoluta = Paths.get(System.getProperty("user.dir"), rutaRelativa);

        try {
            Files.deleteIfExists(rutaAbsoluta);
            System.out.println("Archivo anterior eliminado: " + rutaAbsoluta.toString());
        } catch (IOException e) {
            System.err.println("No se pudo eliminar el archivo anterior: " + rutaAbsoluta);
            e.printStackTrace();
        }
    }
}
