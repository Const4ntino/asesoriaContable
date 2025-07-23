package com.asesoria.contable.app_ac.repository;

import com.asesoria.contable.app_ac.model.entity.Obligacion;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

/**
 * Interfaz para métodos personalizados del repositorio de Obligaciones
 */
public interface ObligacionRepositoryCustom {

    /**
     * Busca obligaciones de un cliente en un rango de fechas
     */
    @Query("SELECT o FROM Obligacion o WHERE o.cliente.id = :clienteId AND o.periodoTributario BETWEEN :fechaInicio AND :fechaFin ORDER BY o.fechaLimite ASC")
    List<Obligacion> findByClienteIdAndPeriodoTributarioBetween(
            @Param("clienteId") Long clienteId,
            @Param("fechaInicio") LocalDate fechaInicio,
            @Param("fechaFin") LocalDate fechaFin);
}
