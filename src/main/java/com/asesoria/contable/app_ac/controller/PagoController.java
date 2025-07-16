package com.asesoria.contable.app_ac.controller;

import com.asesoria.contable.app_ac.model.dto.PagoRequest;
import com.asesoria.contable.app_ac.model.dto.PagoResponse;
import com.asesoria.contable.app_ac.model.entity.Usuario;
import com.asesoria.contable.app_ac.service.PagoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/pagos")
@RequiredArgsConstructor
public class PagoController {

    private final PagoService pagoService;

    @PreAuthorize("hasRole('ADMINISTRADOR') or hasRole('CONTADOR')")
    @GetMapping("/{id}")
    public ResponseEntity<PagoResponse> findById(@PathVariable Long id) {
        PagoResponse pago = pagoService.findById(id);
        return ResponseEntity.ok(pago);
    }

    @PreAuthorize("hasRole('ADMINISTRADOR') or hasRole('CONTADOR')")
    @GetMapping
    public ResponseEntity<List<PagoResponse>> findAll() {
        List<PagoResponse> pagos = pagoService.findAll();
        return ResponseEntity.ok(pagos);
    }

    @PreAuthorize("hasRole('ADMINISTRADOR') or hasRole('CONTADOR')")
    @PostMapping
    public ResponseEntity<PagoResponse> save(@Valid @RequestBody PagoRequest request) {
        PagoResponse newPago = pagoService.save(request);
        return new ResponseEntity<>(newPago, HttpStatus.CREATED);
    }

    @PreAuthorize("hasRole('ADMINISTRADOR') or hasRole('CONTADOR')")
    @PutMapping("/{id}")
    public ResponseEntity<PagoResponse> update(@PathVariable Long id, @Valid @RequestBody PagoRequest request) {
        PagoResponse updatedPago = pagoService.update(id, request);
        return ResponseEntity.ok(updatedPago);
    }

    @PreAuthorize("hasRole('ADMINISTRADOR') or hasRole('CONTADOR')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        pagoService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasRole('CLIENTE')")
    @GetMapping("/mis-pagos")
    public ResponseEntity<List<PagoResponse>> getMisPagos(
            @AuthenticationPrincipal Usuario usuario,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate periodoTributario,
            @RequestParam(required = false) BigDecimal monto,
            @RequestParam(defaultValue = "DESC") String orden
    ) {
        List<PagoResponse> pagos = pagoService.buscarMisPagos(usuario, periodoTributario, monto, orden);
        return ResponseEntity.ok(pagos);
    }

    @PreAuthorize("hasRole('ADMINISTRADOR') or hasRole('CONTADOR')")
    @PatchMapping("/{id}/actualizar-estado")
    public ResponseEntity<PagoResponse> actualizarEstadoPago(@PathVariable Long id, @RequestBody String nuevoEstado) {
        PagoResponse pagoActualizado = pagoService.actualizarEstado(id, nuevoEstado);
        return ResponseEntity.ok(pagoActualizado);
    }


    @PreAuthorize("hasRole('ADMINISTRADOR') or hasRole('CONTADOR')")
    @DeleteMapping("/{id}/rechazar-pago")
    public ResponseEntity<Void> rechazarEstadoPago(@PathVariable Long id, @RequestBody String comentario) {
        pagoService.rechazarPagoCliente(id, comentario);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasRole('CONTADOR')")
    @GetMapping("/mis-clientes/pagos")
    public ResponseEntity<List<PagoResponse>> getUltimosPagosDeMisClientes(
            @AuthenticationPrincipal Usuario usuario,
            @RequestParam(required = false) Integer mes,
            @RequestParam(required = false) String ordenMonto,
            @RequestParam(required = false) String medioPago
    ) {
        List<PagoResponse> pagos = pagoService.buscarUltimosPagosDeMisClientes(usuario, mes, ordenMonto, medioPago);
        return ResponseEntity.ok(pagos);
    }
}
