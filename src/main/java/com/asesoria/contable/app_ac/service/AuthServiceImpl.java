package com.asesoria.contable.app_ac.service;

import com.asesoria.contable.app_ac.exceptions.ClienteNotFoundException;
import com.asesoria.contable.app_ac.exceptions.UsuarioNotFoundException;
import com.asesoria.contable.app_ac.mapper.ClienteMapper;
import com.asesoria.contable.app_ac.model.dto.*;
import com.asesoria.contable.app_ac.model.dto.auth.AuthResponse;
import com.asesoria.contable.app_ac.model.dto.auth.LoginUsuarioRequest;
import com.asesoria.contable.app_ac.model.dto.auth.RegisterClienteRequest;
import com.asesoria.contable.app_ac.model.entity.Cliente;
import com.asesoria.contable.app_ac.model.entity.Usuario;
import com.asesoria.contable.app_ac.utils.enums.Regimen;
import com.asesoria.contable.app_ac.utils.enums.TipoCliente;
import com.asesoria.contable.app_ac.repository.ClienteRepository;
import com.asesoria.contable.app_ac.repository.UsuarioRepository;
import com.asesoria.contable.app_ac.utils.enums.Rol;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UsuarioRepository usuarioRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final ClienteRepository clienteRepository;
    private final ClienteMapper clienteMapper;

    @Override
    public AuthResponse login(LoginUsuarioRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );
        Usuario usuario = usuarioRepository.findByUsername(request.getUsername()).orElseThrow(UsuarioNotFoundException::new);
        String token = jwtService.getToken(usuario);
        return AuthResponse.builder()
                .token(token)
                .rol(usuario.getRol().name())
                .username(usuario.getUsername())
                .build();
    }

    @Override
    public ClienteResponse devolverCliente(Long usuarioId) {
        return clienteRepository.findByUsuarioId(usuarioId)
                .map(clienteMapper::toClienteResponse)
                .orElseThrow(ClienteNotFoundException::new);
    }

    @Transactional
    @Override
    public AuthResponse registerCliente(RegisterClienteRequest request) {
        Usuario nuevoUsuario = Usuario.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .rol(Rol.CLIENTE)
                .estado(true)
                .build();

        Usuario usuarioGuardado = usuarioRepository.save(nuevoUsuario);

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

        Cliente nuevoCliente = new Cliente();
        nuevoCliente.setNombres(request.getNombres());
        nuevoCliente.setApellidos(request.getApellidos());
        nuevoCliente.setRucDni(request.getRucDni());
        nuevoCliente.setEmail(request.getEmail());
        nuevoCliente.setTelefono(request.getTelefono());
        nuevoCliente.setTipoRuc(request.getTipoRuc());
        nuevoCliente.setRegimen(regimen);
        nuevoCliente.setTipoCliente(tipoCliente);
        nuevoCliente.setUsuario(usuarioGuardado);

        clienteRepository.save(nuevoCliente);

        String jwt = jwtService.getToken(usuarioGuardado);

        return AuthResponse.builder()
                .token(jwt)
                .rol(usuarioGuardado.getRol().name())
                .username(usuarioGuardado.getUsername())
                .build();
    }


//    @Override
//    public void registerUsuario(UsuarioRequest request) {
//        Rol rolValido;
//        try {
//            rolValido = Rol.valueOf(request.getRol().toUpperCase());
//        } catch (IllegalArgumentException ex) {
//            throw new IllegalArgumentException("Rol inválido: " + request.getRol());
//        }
//
//        Usuario usuario = Usuario.builder()
//                .username(request.getUsername())
//                .password(passwordEncoder.encode(request.getPassword()))
//                .nombres(request.getNombres())
//                .apellidos(request.getApellidos())
//                .rol(rolValido)
//                .estado(true)
//                .build();
//
//        usuarioRepository.save(usuario);
//    }
}
