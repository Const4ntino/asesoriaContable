package com.asesoria.contable.app_ac.controller;

import com.asesoria.contable.app_ac.model.dto.UsuarioRequest;
import com.asesoria.contable.app_ac.model.dto.UsuarioUpdateRequest;
import com.asesoria.contable.app_ac.model.dto.UsuarioResponse;
import com.asesoria.contable.app_ac.service.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @GetMapping
    public List<UsuarioResponse> findAll() {
        return usuarioService.findAll();
    }

    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @GetMapping("/{id}")
    public UsuarioResponse findById(@PathVariable Long id) {
        return usuarioService.findById(id);
    }

    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @GetMapping("/clientes-libres")
    public ResponseEntity<List<UsuarioResponse>> findAllClientesLibres(@RequestParam("username") String username) {
        List<UsuarioResponse> usuarios = usuarioService.findUsuariosClientesLibres(username);
        if (usuarios.isEmpty()) {
            return ResponseEntity.noContent().build(); // HTTP 204
        }
        return ResponseEntity.ok(usuarios); // HTTP 200 con la lista
    }

    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @GetMapping("/contadores-libres")
    public ResponseEntity<List<UsuarioResponse>> findAllContadoresLibres(@RequestParam("username") String username) {
        List<UsuarioResponse> usuarios = usuarioService.findUsuariosContadoresLibres(username);
        if (usuarios.isEmpty()) {
            return ResponseEntity.noContent().build(); // HTTP 204
        }
        return ResponseEntity.ok(usuarios); // HTTP 200 con la lista
    }

    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @PostMapping
    public ResponseEntity<UsuarioResponse> save(@Valid @RequestBody UsuarioRequest request) {
        UsuarioResponse usuarioResponse = usuarioService.save(request);
        return ResponseEntity
                .created(URI.create("/api/usuarios/" + usuarioResponse.getId()))
                .body(usuarioResponse);
    }

    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @PutMapping("/{id}")
    public UsuarioResponse update(@PathVariable Long id,@Valid @RequestBody UsuarioUpdateRequest request) {
        return usuarioService.update(id, request);
    }

    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        usuarioService.deleteById(id);
    }

    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @GetMapping("/search")
    public List<UsuarioResponse> searchUsuarios(
            @RequestParam(required = false) String searchTerm,
            @RequestParam(required = false) List<String> roles,
            @RequestParam(required = false) Boolean estado,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false) String sortOrder) {
        return usuarioService.searchUsuarios(searchTerm, roles, estado, sortBy, sortOrder);
    }

}
