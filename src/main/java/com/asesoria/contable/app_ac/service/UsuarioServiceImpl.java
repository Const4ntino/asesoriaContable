package com.asesoria.contable.app_ac.service;

import com.asesoria.contable.app_ac.aop.RegistrarBitacora;
import com.asesoria.contable.app_ac.exceptions.UsuarioNotFoundException;
import com.asesoria.contable.app_ac.mapper.UsuarioMapper;
import com.asesoria.contable.app_ac.model.dto.UsuarioRequest;
import com.asesoria.contable.app_ac.model.dto.UsuarioResponse;
import com.asesoria.contable.app_ac.model.dto.UsuarioUpdateRequest;
import com.asesoria.contable.app_ac.model.entity.Usuario;
import com.asesoria.contable.app_ac.repository.UsuarioRepository;
import com.asesoria.contable.app_ac.utils.enums.Accion;
import com.asesoria.contable.app_ac.utils.enums.Modulo;
import com.asesoria.contable.app_ac.utils.enums.Rol;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import jakarta.persistence.criteria.Predicate;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final UsuarioMapper usuarioMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UsuarioResponse findById(Long id) {
        return usuarioRepository.findById(id)
                .map(usuarioMapper::toUsuarioResponse)
                .orElseThrow(UsuarioNotFoundException::new);
    }

    @Override
    public List<UsuarioResponse> findAll() {
        return usuarioRepository.findAll()
                .stream()
                .map(usuarioMapper::toUsuarioResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<UsuarioResponse> findUsuariosClientesLibres(String username) {
        return usuarioRepository.findUsuariosClientesLibres(username)
                .stream()
                .map(usuarioMapper::toUsuarioResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<UsuarioResponse> findUsuariosContadoresLibres(String username) {
        return usuarioRepository.findUsuariosContadoresLibres(username)
                .stream()
                .map(usuarioMapper::toUsuarioResponse)
                .collect(Collectors.toList());
    }

    @Override
    public UsuarioResponse save(UsuarioRequest request) {
        Rol rolValido;
        try {
            rolValido = Rol.valueOf(request.getRol().toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("Rol inválido: " + request.getRol());
        }

        Usuario usuario = Usuario.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .nombres(request.getNombres())
                .apellidos(request.getApellidos())
                .rol(rolValido)
                .estado(request.getEstado() != null ? request.getEstado() : true)
                .build();

        Usuario usuarioGuardado = usuarioRepository.save(usuario);

        return UsuarioResponse.builder()
                .id(usuarioGuardado.getId())
                .username(usuarioGuardado.getUsername())
                .nombres(usuarioGuardado.getNombres())
                .apellidos(usuarioGuardado.getApellidos())
                .rol(usuarioGuardado.getRol().name())
                .estado(usuarioGuardado.isEstado())
                .build();
    }

    @Override
    public UsuarioResponse update(Long id, UsuarioUpdateRequest request) {
        Rol rolValido;
        try {
            rolValido = Rol.valueOf(request.getRol().toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("Rol inválido: " + request.getRol());
        }

        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(UsuarioNotFoundException::new);

        usuario.setUsername(request.getUsername());

        // ✅ Solo actualiza la contraseña si se envía una no vacía
        if (request.getPassword() != null && !request.getPassword().trim().isEmpty()) {
            usuario.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        usuario.setNombres(request.getNombres());
        usuario.setApellidos(request.getApellidos());
        usuario.setRol(rolValido);
        usuario.setEstado(Boolean.TRUE.equals(request.getEstado())); // estado true por defecto

        Usuario usuarioActualizado = usuarioRepository.save(usuario);

        return UsuarioResponse.builder()
                .id(usuarioActualizado.getId())
                .username(usuarioActualizado.getUsername())
                .nombres(usuarioActualizado.getNombres())
                .apellidos(usuarioActualizado.getApellidos())
                .rol(usuarioActualizado.getRol().name())
                .estado(usuarioActualizado.isEstado())
                .build();
    }

    @Override
    @RegistrarBitacora(modulo = Modulo.USUARIO, accion = Accion.ELIMINAR)
    public void deleteById(Long id) {
        if (usuarioRepository.findById(id).isEmpty()) {
            throw new UsuarioNotFoundException();
        }
        usuarioRepository.deleteById(id);
    }

    @Override
    public List<UsuarioResponse> searchUsuarios(String searchTerm, List<String> roles, Boolean estado, String sortBy, String sortOrder) {
        Specification<Usuario> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Filtrar por término de búsqueda (username, nombres, apellidos)
            if (StringUtils.hasText(searchTerm)) {
                String lowerCaseSearchTerm = searchTerm.toLowerCase();
                predicates.add(cb.or(
                        cb.like(cb.lower(root.get("username")), "%" + lowerCaseSearchTerm + "%"),
                        cb.like(cb.lower(root.get("nombres")), "%" + lowerCaseSearchTerm + "%"),
                        cb.like(cb.lower(root.get("apellidos")), "%" + lowerCaseSearchTerm + "%")
                ));
            }

            // Filtrar por roles
            if (roles != null && !roles.isEmpty()) {
                List<Rol> rolesEnum = roles.stream()
                        .map(String::toUpperCase)
                        .filter(r -> {
                            try {
                                Rol.valueOf(r);
                                return true;
                            } catch (IllegalArgumentException e) {
                                return false;
                            }
                        })
                        .map(Rol::valueOf)
                        .collect(Collectors.toList());
                if (!rolesEnum.isEmpty()) {
                    predicates.add(root.get("rol").in(rolesEnum));
                }
            }

            // Filtrar por estado
            if (estado != null) {
                predicates.add(cb.equal(root.get("estado"), estado));
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

        List<Usuario> usuarios = usuarioRepository.findAll(spec, sort);
        return usuarios.stream()
                .map(usuarioMapper::toUsuarioResponse)
                .collect(Collectors.toList());
    }
}
