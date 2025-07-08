package com.asesoria.contable.app_ac.controller;

import com.asesoria.contable.app_ac.model.dto.DeclaracionRequest;
import com.asesoria.contable.app_ac.model.dto.DeclaracionResponse;
import com.asesoria.contable.app_ac.model.entity.Cliente;
import com.asesoria.contable.app_ac.model.entity.Usuario;
import com.asesoria.contable.app_ac.repository.DeclaracionRepository;
import com.asesoria.contable.app_ac.service.ClienteService;
import com.asesoria.contable.app_ac.service.DeclaracionService;
import com.asesoria.contable.app_ac.utils.enums.CronogramaVencimientoSunat;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

@RestController
@RequestMapping("/api/v1/declaraciones")
@RequiredArgsConstructor
public class DeclaracionController {

    private final DeclaracionService declaracionService;
    private final ClienteService clienteService;
    private final DeclaracionRepository declaracionRepository;

    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @GetMapping("/{id}")
    public ResponseEntity<DeclaracionResponse> findById(@PathVariable Long id) {
        DeclaracionResponse declaracion = declaracionService.findById(id);
        return ResponseEntity.ok(declaracion);
    }

    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @GetMapping
    public ResponseEntity<List<DeclaracionResponse>> findAll() {
        List<DeclaracionResponse> declaraciones = declaracionService.findAll();
        return ResponseEntity.ok(declaraciones);
    }

    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @PostMapping
    public ResponseEntity<DeclaracionResponse> save(@Valid @RequestBody DeclaracionRequest request) {
        DeclaracionResponse newDeclaracion = declaracionService.save(request);
        return new ResponseEntity<>(newDeclaracion, HttpStatus.CREATED);
    }

    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @PutMapping("/{id}")
    public ResponseEntity<DeclaracionResponse> update(@PathVariable Long id, @Valid @RequestBody DeclaracionRequest request) {
        DeclaracionResponse updatedDeclaracion = declaracionService.update(id, request);
        return ResponseEntity.ok(updatedDeclaracion);
    }

    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        declaracionService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'CONTADOR')")
    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<List<DeclaracionResponse>> getDeclaracionesByClienteId(@PathVariable Long clienteId) {
        List<DeclaracionResponse> declaraciones = declaracionService.findByClienteId(clienteId);
        return ResponseEntity.ok(declaraciones);
    }

    @PreAuthorize("hasRole('CLIENTE')")
    @GetMapping("/actual")
    public ResponseEntity<DeclaracionResponse> generarDeclaracionSiNoExiste(@AuthenticationPrincipal Usuario usuario) {
        Cliente cliente = clienteService.findEntityByUsuarioId(usuario.getId());
        String ruc = cliente.getRucDni(); // asumiendo que lo tienes

        // 1. Periodo tributario a declarar (el mes anterior al actual)
        YearMonth periodo = YearMonth.now().minusMonths(1); // Ej: 2025-06

        // 2. Obtener último dígito del RUC
        int ultimoDigito = Character.getNumericValue(ruc.charAt(ruc.length() - 1));

        // 3. Calcular fecha límite
        LocalDate fechaLimite = CronogramaVencimientoSunat.getFechaVencimiento(periodo.toString(), ultimoDigito);

        // 4. Verificar si ya existe
        boolean existe = declaracionRepository.existsByClienteIdAndPeriodoTributarioAndTipo(cliente.getId(), periodo.atDay(1), "REGULAR");
        if (existe) {
            return ResponseEntity.ok().build(); // ya existe, no se crea nada
        }

        // 5. Crear nueva declaración PENDIENTE
        Declaracion declaracion = new Declaracion();
        declaracion.setCliente(cliente);
        declaracion.setPeriodoTributario(periodo.atDay(1));
        declaracion.setTipo("REGULAR");
        declaracion.setFechaLimite(fechaLimite);
        declaracion.setEstadoCliente("PENDIENTE");
        declaracion.setEstadoContador("PENDIENTE");

        declaracionRepository.save(declaracion);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
