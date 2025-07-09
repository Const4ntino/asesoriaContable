package com.asesoria.contable.app_ac.service;

import com.asesoria.contable.app_ac.model.dto.DeclaracionRequest;
import com.asesoria.contable.app_ac.model.dto.DeclaracionResponse;
import com.asesoria.contable.app_ac.model.dto.PeriodoVencimientoResponse;
import com.asesoria.contable.app_ac.model.entity.Usuario;
import com.asesoria.contable.app_ac.utils.enums.DeclaracionEstado;
import com.asesoria.contable.app_ac.utils.enums.EstadoContador;

import java.time.LocalDate;
import java.util.List;

public interface DeclaracionService {

    DeclaracionResponse findById(Long id);
    List<DeclaracionResponse> findAll();
    DeclaracionResponse save(DeclaracionRequest request);
    DeclaracionResponse update(Long id, DeclaracionRequest request);
    void deleteById(Long id);
    List<DeclaracionResponse> findByClienteId(Long clienteId);

    DeclaracionResponse generarDeclaracionSiNoExiste(Usuario usuario);

    List<DeclaracionResponse> buscarMisDeclaraciones(Usuario usuario, LocalDate fechaInicio, LocalDate fechaFin, DeclaracionEstado estado, EstadoContador estadoContador);

    DeclaracionResponse notificarContador(Long declaracionId, Usuario usuario);

    DeclaracionResponse findFirstCreadaByUsuario(Usuario usuario);

    PeriodoVencimientoResponse getPeriodoActualYFechaVencimiento(Usuario usuario);

    List<DeclaracionResponse> getLatestDeclarationsForMyClients(Usuario usuario);
}
