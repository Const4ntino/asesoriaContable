package com.asesoria.contable.app_ac.service;

import com.asesoria.contable.app_ac.exceptions.ClienteNotFoundException;
import com.asesoria.contable.app_ac.exceptions.ContadorNotFoundException;
import com.asesoria.contable.app_ac.exceptions.UsuarioNotFoundException;
import com.asesoria.contable.app_ac.mapper.ClienteMapper;
import com.asesoria.contable.app_ac.model.dto.ClienteRequest;
import com.asesoria.contable.app_ac.model.dto.ClienteResponse;
import com.asesoria.contable.app_ac.model.entity.Cliente;
import com.asesoria.contable.app_ac.model.entity.Contador;
import com.asesoria.contable.app_ac.model.entity.Usuario;
import com.asesoria.contable.app_ac.utils.enums.Regimen;
import com.asesoria.contable.app_ac.utils.enums.TipoCliente;
import com.asesoria.contable.app_ac.repository.ClienteRepository;
import com.asesoria.contable.app_ac.repository.ContadorRepository;
import com.asesoria.contable.app_ac.repository.UsuarioRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ClienteServiceImpl implements ClienteService {

    private final ClienteRepository clienteRepository;
    private final UsuarioRepository usuarioRepository;
    private final ContadorRepository contadorRepository;
    private final ClienteMapper clienteMapper;

    @Override
    public ClienteResponse findById(Long id) {
        return clienteRepository.findById(id)
                .map(clienteMapper::toClienteResponse)
                .orElseThrow(ClienteNotFoundException::new);
    }

    @Override
    public List<ClienteResponse> findAll() {
        return clienteRepository.findAll()
                .stream()
                .map(clienteMapper::toClienteResponse)
                .collect(Collectors.toList());
    }

    @Override
    public ClienteResponse findByUsuarioId(Long usuarioId) {
        return clienteRepository.findByUsuarioId(usuarioId)
                .map(clienteMapper::toClienteResponse)
                .orElseThrow(ClienteNotFoundException::new);
    }

    @Override
    public List<ClienteResponse> findAllByContadorId(Long contadorId) {
        Contador contador = contadorRepository.findById(contadorId)
                .orElseThrow(UsuarioNotFoundException::new);

        List<Cliente> clientes = clienteRepository.findAllByContador(contador);

        return clientes.stream()
                .map(clienteMapper::toClienteResponse)
                .collect(Collectors.toList());
    }

    @Override
    public ClienteResponse save(ClienteRequest request) {
        Usuario usuario = null;
        if (request.getIdUsuario() != null) {
            usuario = usuarioRepository.findById(request.getIdUsuario())
                    .orElseThrow(UsuarioNotFoundException::new);
        }

        Contador contador = null;
        if (request.getIdContador() != null) {
            contador = contadorRepository.findById(request.getIdContador())
                    .orElseThrow(ContadorNotFoundException::new);
        }

        Regimen regimen = null;
        if (request.getRegimen() != null) {
            try {
                regimen = Regimen.valueOf(request.getRegimen().toUpperCase());
            } catch (IllegalArgumentException e) {
                // Manejar el caso de un valor de regimen inválido, si es necesario
            }
        }

        TipoCliente tipoCliente = null;
        if (request.getTipoCliente() != null) {
            try {
                tipoCliente = TipoCliente.valueOf(request.getTipoCliente().toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Tipo de cliente inválido: " + request.getTipoCliente());
            }
        }

        Cliente cliente = Cliente.builder()
                .nombres(request.getNombres())
                .apellidos(request.getApellidos())
                .rucDni(request.getRucDni())
                .email(request.getEmail())
                .telefono(request.getTelefono())
                .tipoRuc(request.getTipoRuc())
                .regimen(regimen)
                .tipoCliente(tipoCliente)
                .usuario(usuario)
                .contador(contador)
                .build();

        Cliente clienteGuardado = clienteRepository.save(cliente);
        return clienteMapper.toClienteResponse(clienteGuardado);
    }

    @Override
    public ClienteResponse update(Long id, ClienteRequest request) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(ClienteNotFoundException::new);

        Usuario usuario = null;
        if (request.getIdUsuario() != null) {
            usuario = usuarioRepository.findById(request.getIdUsuario())
                    .orElseThrow(UsuarioNotFoundException::new);
        }

        Contador contador = null;
        if (request.getIdContador() != null) {
            contador = contadorRepository.findById(request.getIdContador())
                    .orElseThrow(ContadorNotFoundException::new);
        }

        Regimen regimen = null;
        if (request.getRegimen() != null) {
            try {
                regimen = Regimen.valueOf(request.getRegimen().toUpperCase());
            } catch (IllegalArgumentException e) {
                // Manejar el caso de un valor de regimen inválido, si es necesario
            }
        }

        TipoCliente tipoCliente = null;
        if (request.getTipoCliente() != null) {
            try {
                tipoCliente = TipoCliente.valueOf(request.getTipoCliente().toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Tipo de cliente inválido: " + request.getTipoCliente());
            }
        }

        cliente.setNombres(request.getNombres());
        cliente.setApellidos(request.getApellidos());
        cliente.setRucDni(request.getRucDni());
        cliente.setEmail(request.getEmail());
        cliente.setTelefono(request.getTelefono());
        cliente.setTipoRuc(request.getTipoRuc());
        cliente.setRegimen(regimen);
        cliente.setTipoCliente(tipoCliente);
        cliente.setUsuario(usuario);
        cliente.setContador(contador);

        Cliente clienteActualizado = clienteRepository.save(cliente);
        return clienteMapper.toClienteResponse(clienteActualizado);
    }

    @Override
    public void deleteById(Long id) {
        if (clienteRepository.findById(id).isEmpty()) {
            throw new ClienteNotFoundException();
        }
        clienteRepository.deleteById(id);
    }

    @Override
    @Transactional
    public ClienteResponse asignarContador(Long clienteId, Long contadorId) {
        Cliente cliente = clienteRepository.findById(clienteId)
                .orElseThrow(ClienteNotFoundException::new);

        Contador contador = contadorRepository.findById(contadorId)
                .orElseThrow(ContadorNotFoundException::new);

        cliente.setContador(contador);
        return clienteMapper.toClienteResponse(clienteRepository.save(cliente));
    }

    @Override
    @Transactional
    public void desasignarContador(Long clienteId) {
        Cliente cliente = clienteRepository.findById(clienteId)
                .orElseThrow(ClienteNotFoundException::new);

        cliente.setContador(null);
        clienteRepository.save(cliente);
    }

    @Override
    public List<ClienteResponse> searchClientes(String searchTerm, String tipoClienteStr, String regimenStr, String sortBy, String sortOrder) {
        Specification<Cliente> spec = (root, query, cb) -> {
            List<jakarta.persistence.criteria.Predicate> predicates = new ArrayList<>();

            // Filtrar por tipo de cliente
            if (StringUtils.hasText(tipoClienteStr)) {
                try {
                    TipoCliente tipoClienteEnum = TipoCliente.valueOf(tipoClienteStr.toUpperCase());
                    predicates.add(cb.equal(root.get("tipoCliente"), tipoClienteEnum));
                } catch (IllegalArgumentException e) {
                    // Ignorar o manejar error de tipo de cliente inválido
                }
            }

            // Filtrar por régimen (solo si es PERSONA_JURIDICA y se proporciona regimenStr)
            if (StringUtils.hasText(regimenStr)) {
                try {
                    Regimen regimenEnum = Regimen.valueOf(regimenStr.toUpperCase());
                    predicates.add(cb.equal(root.get("regimen"), regimenEnum));
                } catch (IllegalArgumentException e) {
                    // Ignorar o manejar error de régimen inválido
                }
            }

            // Buscar por searchTerm
            if (StringUtils.hasText(searchTerm)) {
                String lowerCaseSearchTerm = searchTerm.toLowerCase();
                jakarta.persistence.criteria.Predicate searchPredicate = cb.or(
                        cb.like(cb.lower(root.get("nombres")), "%" + lowerCaseSearchTerm + "%"),
                        cb.like(cb.lower(root.get("rucDni")), "%" + lowerCaseSearchTerm + "%")
                );

                // Si es PERSONA_NATURAL, también buscar por apellidos
                if (StringUtils.hasText(tipoClienteStr) && tipoClienteStr.equalsIgnoreCase("PERSONA_NATURAL")) {
                    searchPredicate = cb.or(searchPredicate,
                            cb.like(cb.lower(root.get("apellidos")), "%" + lowerCaseSearchTerm + "%")
                    );
                } else if (!StringUtils.hasText(tipoClienteStr)) { // Si no se especifica tipo, buscar en apellidos también
                    searchPredicate = cb.or(searchPredicate,
                            cb.like(cb.lower(root.get("apellidos")), "%" + lowerCaseSearchTerm + "%")
                    );
                }
                predicates.add(searchPredicate);
            }

            return cb.and(predicates.toArray(new jakarta.persistence.criteria.Predicate[0]));
        };

        Sort sort = Sort.unsorted();
        if (StringUtils.hasText(sortBy)) {
            Sort.Direction direction = Sort.Direction.ASC;
            if (StringUtils.hasText(sortOrder) && sortOrder.equalsIgnoreCase("DESC")) {
                direction = Sort.Direction.DESC;
            }
            sort = Sort.by(direction, sortBy);
        }

        List<Cliente> clientes = clienteRepository.findAll(spec, sort);
        return clientes.stream()
                .map(clienteMapper::toClienteResponse)
                .collect(Collectors.toList());
    }
}
