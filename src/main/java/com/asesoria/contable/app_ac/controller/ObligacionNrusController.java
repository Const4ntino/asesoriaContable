package com.asesoria.contable.app_ac.controller;

import com.asesoria.contable.app_ac.model.dto.ObligacionNrusRequest;
import com.asesoria.contable.app_ac.model.dto.ObligacionNrusResponse;
import com.asesoria.contable.app_ac.service.ObligacionNrusService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;

@AllArgsConstructor
@RestController
@RequestMapping("/api/v1/obligaciones-nrus")
public class ObligacionNrusController {

    private final ObligacionNrusService obligacionNrusService;

    @PreAuthorize("hasAnyRole('CLIENTE', 'CONTADOR')")
    @GetMapping
    public ResponseEntity<List<ObligacionNrusResponse>> getAll() {
        return new ResponseEntity<>(obligacionNrusService.getAll(), HttpStatus.OK);
    }

    @PreAuthorize("hasAnyRole('CLIENTE', 'CONTADOR')")
    @GetMapping("/{id}")
    public ResponseEntity<ObligacionNrusResponse> getOne(@PathVariable Long id) {
        return new ResponseEntity<>(obligacionNrusService.getOne(id), HttpStatus.OK);
    }

    @PreAuthorize("hasAnyRole('CLIENTE', 'CONTADOR')")
    @PostMapping
    public ResponseEntity<ObligacionNrusResponse> save(@RequestBody ObligacionNrusRequest obligacionNrusRequest) {
        return new ResponseEntity<>(obligacionNrusService.save(obligacionNrusRequest), HttpStatus.CREATED);
    }

    @PreAuthorize("hasAnyRole('CLIENTE', 'CONTADOR')")
    @PutMapping("/{id}")
    public ResponseEntity<ObligacionNrusResponse> update(@PathVariable Long id, @RequestBody ObligacionNrusRequest obligacionNrusRequest) {
        return new ResponseEntity<>(obligacionNrusService.update(id, obligacionNrusRequest), HttpStatus.OK);
    }

    @PreAuthorize("hasAnyRole('CLIENTE', 'CONTADOR')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        obligacionNrusService.delete(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
