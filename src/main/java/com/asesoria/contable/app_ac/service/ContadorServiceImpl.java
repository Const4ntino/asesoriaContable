package com.asesoria.contable.app_ac.service;

import com.asesoria.contable.app_ac.exceptions.ContadorNotFoundException;
import com.asesoria.contable.app_ac.exceptions.UsuarioNotFoundException;
import com.asesoria.contable.app_ac.mapper.ContadorMapper;
import com.asesoria.contable.app_ac.model.dto.ContadorRequest;
import com.asesoria.contable.app_ac.model.dto.ContadorResponse;
import com.asesoria.contable.app_ac.model.entity.Contador;
import com.asesoria.contable.app_ac.model.entity.Usuario;
import com.asesoria.contable.app_ac.repository.ClienteRepository;
import com.asesoria.contable.app_ac.repository.ContadorRepository;
import com.asesoria.contable.app_ac.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ContadorServiceImpl implements ContadorService {

    private final UsuarioRepository usuarioRepository;
    private final ContadorRepository contadorRepository;
    private final ClienteRepository clienteRepository;
    private final ContadorMapper contadorMapper;

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
}
