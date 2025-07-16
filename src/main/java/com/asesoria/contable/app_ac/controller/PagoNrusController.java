package com.asesoria.contable.app_ac.controller;

import com.asesoria.contable.app_ac.model.dto.PagoNrusRequest;
import com.asesoria.contable.app_ac.model.dto.PagoNrusResponse;
import com.asesoria.contable.app_ac.service.PagoNrusService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;

@AllArgsConstructor
@RestController
@RequestMapping("/api/v1/pagos-nrus")
public class PagoNrusController {

    private final PagoNrusService pagoNrusService;

    @PreAuthorize("hasAnyRole('CLIENTE', 'CONTADOR')")
    @GetMapping
    public ResponseEntity<List<PagoNrusResponse>> getAll() {
        return new ResponseEntity<>(pagoNrusService.getAll(), HttpStatus.OK);
    }

    @PreAuthorize("hasAnyRole('CLIENTE', 'CONTADOR')")
    @GetMapping("/{id}")
    public ResponseEntity<PagoNrusResponse> getOne(@PathVariable Long id) {
        return new ResponseEntity<>(pagoNrusService.getOne(id), HttpStatus.OK);
    }

    @PreAuthorize("hasAnyRole('CLIENTE', 'CONTADOR')")
    @PostMapping
    public ResponseEntity<PagoNrusResponse> save(@RequestBody PagoNrusRequest pagoNrusRequest) {
        return new ResponseEntity<>(pagoNrusService.save(pagoNrusRequest), HttpStatus.CREATED);
    }

    @PreAuthorize("hasAnyRole('CLIENTE', 'CONTADOR')")
    @PutMapping("/{id}")
    public ResponseEntity<PagoNrusResponse> update(@PathVariable Long id, @RequestBody PagoNrusRequest pagoNrusRequest) {
        return new ResponseEntity<>(pagoNrusService.update(id, pagoNrusRequest), HttpStatus.OK);
    }

    @PreAuthorize("hasAnyRole('CLIENTE', 'CONTADOR')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        pagoNrusService.delete(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PreAuthorize("hasAnyRole('CLIENTE', 'CONTADOR')")
    @PostMapping("/{idObligacionNrus}/registrar")
    public ResponseEntity<PagoNrusResponse> registrarPago(
            @PathVariable Long idObligacionNrus,
            @RequestBody PagoNrusRequest pagoNrusRequest) {
        return new ResponseEntity<>(pagoNrusService.registrarPago(idObligacionNrus, pagoNrusRequest), HttpStatus.CREATED);
    }
}
