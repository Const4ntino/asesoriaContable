package com.asesoria.contable.app_ac.service;

import com.asesoria.contable.app_ac.aop.RegistrarBitacora;
import com.asesoria.contable.app_ac.exceptions.ContadorNotFoundException;
import com.asesoria.contable.app_ac.exceptions.UsuarioNotFoundException;
import com.asesoria.contable.app_ac.mapper.ClienteMapper;
import com.asesoria.contable.app_ac.mapper.ContadorMapper;
import com.asesoria.contable.app_ac.model.dto.ClienteConMetricasResponse;
import com.asesoria.contable.app_ac.model.dto.ContadorRequest;
import com.asesoria.contable.app_ac.model.dto.ContadorResponse;
import com.asesoria.contable.app_ac.model.entity.Cliente;
import com.asesoria.contable.app_ac.model.entity.Contador;
import com.asesoria.contable.app_ac.model.entity.Usuario;
import com.asesoria.contable.app_ac.repository.ClienteRepository;
import com.asesoria.contable.app_ac.repository.ContadorRepository;
import com.asesoria.contable.app_ac.repository.UsuarioRepository;
import com.asesoria.contable.app_ac.repository.EgresoRepository;
import com.asesoria.contable.app_ac.repository.IngresoRepository;
import com.asesoria.contable.app_ac.utils.enums.Accion;
import com.asesoria.contable.app_ac.utils.enums.Modulo;
import com.asesoria.contable.app_ac.utils.enums.Regimen;
import com.asesoria.contable.app_ac.utils.enums.TipoCliente;
import com.asesoria.contable.app_ac.specification.ContadorSpecification;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ContadorServiceImpl implements ContadorService {

    private final UsuarioRepository usuarioRepository;
    private final ContadorRepository contadorRepository;
    private final ClienteRepository clienteRepository;
    private final ClienteMapper clienteMapper;
    private final ContadorMapper contadorMapper;
    private final IngresoRepository ingresoRepository;
    private final EgresoRepository egresoRepository;

    @Override
    public ContadorResponse findById(Long id) {
        return contadorRepository.findById(id)
                .map(contadorMapper::toContadorResponse)
                .orElseThrow(ContadorNotFoundException::new);
    }

    @Override
    public List<ContadorResponse> findAll() {
        List<Contador> contadores = contadorRepository.findAll();
        List<Long> contadorIds = contadores.stream().map(Contador::getId).collect(Collectors.toList());

        Map<Long, Long> clienteCounts = clienteRepository.countClientesByContadorIds(contadorIds).stream()
                .collect(Collectors.toMap(result -> (Long) result[0], result -> (Long) result[1]));

        return contadores.stream().map(contador -> {
            ContadorResponse response = contadorMapper.toContadorResponse(contador);
            response.setNumeroClientes(clienteCounts.getOrDefault(contador.getId(), 0L));
            return response;
        }).collect(Collectors.toList());
    }

    @Override
    public ContadorResponse findByUsuarioId(Long usuarioId) {
        return contadorRepository.findByUsuarioId(usuarioId)
                .map(contadorMapper::toContadorResponse)
                .orElseThrow(ContadorNotFoundException::new);
    }

    @Override
    public ContadorResponse save(ContadorRequest request) {
        Usuario usuario = null;

        if (request.getIdUsuario() != null) {
            usuario = usuarioRepository.findById(request.getIdUsuario())
                    .orElseThrow(UsuarioNotFoundException::new);
        }

        Contador contador = Contador.builder()
                .nombres(request.getNombres())
                .apellidos(request.getApellidos())
                .dni(request.getDni())
                .telefono(request.getTelefono())
                .email(request.getEmail())
                .especialidad(request.getEspecialidad())
                .nroColegiatura(request.getNroColegiatura())
                .usuario(usuario) // puede ser null
                .build();

        return contadorMapper.toContadorResponse(contadorRepository.save(contador));
    }

    @Override
    public ContadorResponse update(Long id, ContadorRequest request) {
        return contadorRepository.findById(id)
                .map(contador -> {
                    contador.setNombres(request.getNombres());
                    contador.setApellidos(request.getApellidos());
                    contador.setDni(request.getDni());
                    contador.setTelefono(request.getTelefono());
                    contador.setEmail(request.getEmail());
                    contador.setEspecialidad(request.getEspecialidad());
                    contador.setNroColegiatura(request.getNroColegiatura());

                    if (request.getIdUsuario() != null) {
                        Usuario usuario = usuarioRepository.findById(request.getIdUsuario())
                                .orElseThrow(UsuarioNotFoundException::new);
                        contador.setUsuario(usuario);
                    } else {
                        contador.setUsuario(null); // Si decides permitir quitar la asociación
                    }

                    return contadorMapper.toContadorResponse(contadorRepository.save(contador));
                })
                .orElseThrow(ContadorNotFoundException::new);
    }

    @Override
    @RegistrarBitacora(modulo = Modulo.CONTADOR, accion = Accion.ELIMINAR)
    public void deleteById(Long id) {
        if (contadorRepository.findById(id).isEmpty()) {
            throw new ContadorNotFoundException();
        }
        contadorRepository.deleteById(id);
    }

    @Override
    public List<ContadorResponse> searchContadores(String searchTerm, String sortBy, String sortOrder) {
        Specification<Contador> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (StringUtils.hasText(searchTerm)) {
                String lowerCaseSearchTerm = searchTerm.toLowerCase();
                predicates.add(cb.or(
                        cb.like(cb.lower(root.get("nombres")), "%" + lowerCaseSearchTerm + "%"),
                        cb.like(cb.lower(root.get("apellidos")), "%" + lowerCaseSearchTerm + "%"),
                        cb.like(cb.lower(root.get("dni")), "%" + lowerCaseSearchTerm + "%")
                ));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        Sort sort = Sort.unsorted();
        if (StringUtils.hasText(sortBy)) {
            Sort.Direction direction = Sort.Direction.ASC;
            if (StringUtils.hasText(sortOrder) && sortOrder.equalsIgnoreCase("DESC")) {
                direction = Sort.Direction.DESC;
            }
            sort = Sort.by(direction, sortBy);
        }

        List<Contador> contadores = contadorRepository.findAll(spec, sort);

        // Reutilizar la lógica de conteo de clientes
        List<Long> contadorIds = contadores.stream().map(Contador::getId).collect(Collectors.toList());
        Map<Long, Long> clienteCounts = clienteRepository.countClientesByContadorIds(contadorIds).stream()
                .collect(Collectors.toMap(result -> (Long) result[0], result -> (Long) result[1]));

        return contadores.stream().map(contador -> {
            ContadorResponse response = contadorMapper.toContadorResponse(contador);
            response.setNumeroClientes(clienteCounts.getOrDefault(contador.getId(), 0L));
            return response;
        }).collect(Collectors.toList());
    }

    @Override
    public List<ClienteConMetricasResponse> getClientesNaturalesConMetricas(Usuario usuario, String regimen, String rucDni, String nombres, String sortBy, String sortOrder) {
        return getClientesConMetricas(usuario, TipoCliente.PERSONA_NATURAL, regimen, rucDni, nombres, sortBy, sortOrder);
    }

    @Override
    public List<ClienteConMetricasResponse> getClientesJuridicosConMetricas(Usuario usuario, String regimen, String rucDni, String nombres, String sortBy, String sortOrder) {
        return getClientesConMetricas(usuario, TipoCliente.PERSONA_JURIDICA, regimen, rucDni, nombres, sortBy, sortOrder);
    }

    private List<ClienteConMetricasResponse> getClientesConMetricas(Usuario usuario, TipoCliente tipoCliente, String regimen, String rucDni, String nombres, String sortBy, String sortOrder) {
        Contador contador = contadorRepository.findByUsuarioId(usuario.getId())
                .orElseThrow(ContadorNotFoundException::new);

        Specification<Cliente> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.equal(root.get("contador"), contador));
            predicates.add(cb.equal(root.get("tipoCliente"), tipoCliente));

            if (StringUtils.hasText(regimen)) {
                predicates.add(cb.equal(root.get("regimen"), Regimen.valueOf(regimen.toUpperCase())));
            }
            if (StringUtils.hasText(rucDni)) {
                predicates.add(cb.like(cb.lower(root.get("rucDni")), "%" + rucDni.toLowerCase() + "%"));
            }
            if (StringUtils.hasText(nombres)) {
                String lowerCaseNombres = nombres.toLowerCase();
                predicates.add(cb.or(
                        cb.like(cb.lower(root.get("nombres")), "%" + lowerCaseNombres + "%"),
                        cb.like(cb.lower(root.get("apellidos")), "%" + lowerCaseNombres + "%")
                ));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        // Apply sorting for direct Cliente fields at DB level
        Sort sort = Sort.unsorted();
        if (StringUtils.hasText(sortBy)) {
            Sort.Direction direction = Sort.Direction.ASC;
            if (StringUtils.hasText(sortOrder) && sortOrder.equalsIgnoreCase("DESC")) {
                direction = Sort.Direction.DESC;
            }
            // Only apply sorting for direct fields here
            if (sortBy.equals("nombres") || sortBy.equals("apellidos") || sortBy.equals("rucDni") || sortBy.equals("regimen")) {
                sort = Sort.by(direction, sortBy);
            }
        }

        List<Cliente> clientes = clienteRepository.findAll(spec, sort);

        YearMonth mesActual = YearMonth.now();
        LocalDate inicioMes = mesActual.atDay(1);
        LocalDate finMes = mesActual.atEndOfMonth();

        List<ClienteConMetricasResponse> results = clientes.stream().map(cliente -> {
            BigDecimal totalIngresos = ingresoRepository.sumMontoByClienteIdAndFechaBetween(cliente.getId(), inicioMes, finMes);
            BigDecimal totalEgresos = egresoRepository.sumMontoByClienteIdAndFechaBetween(cliente.getId(), inicioMes, finMes);
            BigDecimal utilidad = totalIngresos.subtract(totalEgresos);

            ClienteConMetricasResponse response = new ClienteConMetricasResponse();
            response.setCliente(clienteMapper.toClienteResponse(cliente));
            response.setTotalIngresosMesActual(totalIngresos);
            response.setTotalEgresosMesActual(totalEgresos);
            response.setUtilidadMesActual(utilidad);

            return response;
        }).collect(Collectors.toList());

        // Apply in-memory sorting for calculated fields
        if (StringUtils.hasText(sortBy)) {
            Comparator<ClienteConMetricasResponse> comparator = null;
            switch (sortBy) {
                case "totalIngresosMesActual":
                    comparator = Comparator.comparing(ClienteConMetricasResponse::getTotalIngresosMesActual);
                    break;
                case "totalEgresosMesActual":
                    comparator = Comparator.comparing(ClienteConMetricasResponse::getTotalEgresosMesActual);
                    break;
                case "utilidadMesActual":
                    comparator = Comparator.comparing(ClienteConMetricasResponse::getUtilidadMesActual);
                    break;
                // For other fields, sorting is already applied by JPA
            }

            if (comparator != null) {
                if (StringUtils.hasText(sortOrder) && sortOrder.equalsIgnoreCase("DESC")) {
                    results.sort(comparator.reversed());
                } else {
                    results.sort(comparator);
                }
            }
        }

        return results;
    }

    @Override
    public Page<ContadorResponse> findContadoresConClientes(String search, Pageable pageable) {
        // Usar el Specification para filtrar contadores con clientes
        Specification<Contador> spec = ContadorSpecification.contadoresConClientes(search);
        Page<Contador> contadoresPage = contadorRepository.findAll(spec, pageable);
        
        return contadoresPage.map(contador -> {
            ContadorResponse response = contadorMapper.toContadorResponse(contador);
            
            // Obtener el número de clientes para este contador
            Long clienteCount = clienteRepository.countByContadorId(contador.getId());
            response.setNumeroClientes(clienteCount);
            
            return response;
        });
    }
}
