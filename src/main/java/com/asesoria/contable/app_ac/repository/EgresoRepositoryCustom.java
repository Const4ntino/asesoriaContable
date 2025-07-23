package com.asesoria.contable.app_ac.repository;

import com.asesoria.contable.app_ac.model.entity.Egreso;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Interfaz para métodos personalizados del repositorio de Egresos
 */
public interface EgresoRepositoryCustom {

    /**
     * Suma el monto total de egresos para un cliente en un rango de fechas
     */
    @Query("SELECT SUM(e.monto) FROM Egreso e WHERE e.cliente.id = :clienteId AND e.fecha BETWEEN :fechaInicio AND :fechaFin")
    BigDecimal sumMontoByClienteIdAndFechaBetween(
            @Param("clienteId") Long clienteId,
            @Param("fechaInicio") LocalDate fechaInicio,
            @Param("fechaFin") LocalDate fechaFin);

    /**
     * Suma el monto de IGV de egresos para un cliente en un rango de fechas
     */
    @Query("SELECT SUM(e.montoIgv) FROM Egreso e WHERE e.cliente.id = :clienteId AND e.fecha BETWEEN :fechaInicio AND :fechaFin")
    BigDecimal sumMontoIgvByClienteIdAndFechaBetween(
            @Param("clienteId") Long clienteId,
            @Param("fechaInicio") LocalDate fechaInicio,
            @Param("fechaFin") LocalDate fechaFin);

    /**
     * Obtiene la distribución de egresos por tipo tributario
     */
    @Query("SELECT e.tipoTributario, SUM(e.monto) FROM Egreso e " +
           "WHERE e.cliente.id = :clienteId AND e.fecha BETWEEN :fechaInicio AND :fechaFin " +
           "GROUP BY e.tipoTributario")
    List<Object[]> findDistribucionPorTipoTributario(
            @Param("clienteId") Long clienteId,
            @Param("fechaInicio") LocalDate fechaInicio,
            @Param("fechaFin") LocalDate fechaFin);

    /**
     * Obtiene la distribución de egresos por tipo de contabilidad
     */
    @Query("SELECT e.tipoContabilidad, SUM(e.monto) FROM Egreso e " +
           "WHERE e.cliente.id = :clienteId AND e.fecha BETWEEN :fechaInicio AND :fechaFin " +
           "GROUP BY e.tipoContabilidad")
    List<Object[]> findDistribucionPorTipoContabilidad(
            @Param("clienteId") Long clienteId,
            @Param("fechaInicio") LocalDate fechaInicio,
            @Param("fechaFin") LocalDate fechaFin);

    /**
     * Obtiene los últimos 5 egresos de un cliente ordenados por fecha descendente
     */
    List<Egreso> findTop5ByClienteIdOrderByFechaDesc(Long clienteId);
}
