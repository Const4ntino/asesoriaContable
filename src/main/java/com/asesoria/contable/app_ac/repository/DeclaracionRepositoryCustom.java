package com.asesoria.contable.app_ac.repository;

import com.asesoria.contable.app_ac.model.entity.Declaracion;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

/**
 * Interfaz para métodos personalizados del repositorio de Declaraciones
 */
public interface DeclaracionRepositoryCustom {

    /**
     * Busca declaraciones de un cliente en un rango de fechas
     */
    @Query("SELECT d FROM Declaracion d WHERE d.cliente.id = :clienteId AND d.periodoTributario BETWEEN :fechaInicio AND :fechaFin ORDER BY d.periodoTributario DESC")
    List<Declaracion> findByClienteIdAndPeriodoTributarioBetween(
            @Param("clienteId") Long clienteId,
            @Param("fechaInicio") LocalDate fechaInicio,
            @Param("fechaFin") LocalDate fechaFin);
}
