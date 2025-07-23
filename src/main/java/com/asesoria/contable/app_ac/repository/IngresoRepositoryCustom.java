package com.asesoria.contable.app_ac.repository;

import com.asesoria.contable.app_ac.model.entity.Ingreso;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Interfaz para métodos personalizados del repositorio de Ingresos
 */
public interface IngresoRepositoryCustom {

    /**
     * Suma el monto total de ingresos para un cliente en un rango de fechas
     */
    @Query("SELECT SUM(i.monto) FROM Ingreso i WHERE i.cliente.id = :clienteId AND i.fecha BETWEEN :fechaInicio AND :fechaFin")
    BigDecimal sumMontoByClienteIdAndFechaBetween(
            @Param("clienteId") Long clienteId,
            @Param("fechaInicio") LocalDate fechaInicio,
            @Param("fechaFin") LocalDate fechaFin);

    /**
     * Suma el monto de IGV de ingresos para un cliente en un rango de fechas
     */
    @Query("SELECT SUM(i.montoIgv) FROM Ingreso i WHERE i.cliente.id = :clienteId AND i.fecha BETWEEN :fechaInicio AND :fechaFin")
    BigDecimal sumMontoIgvByClienteIdAndFechaBetween(
            @Param("clienteId") Long clienteId,
            @Param("fechaInicio") LocalDate fechaInicio,
            @Param("fechaFin") LocalDate fechaFin);

    /**
     * Obtiene la distribución de ingresos por tipo tributario
     */
    @Query("SELECT i.tipoTributario, SUM(i.monto) FROM Ingreso i " +
           "WHERE i.cliente.id = :clienteId AND i.fecha BETWEEN :fechaInicio AND :fechaFin " +
           "GROUP BY i.tipoTributario")
    List<Object[]> findDistribucionPorTipoTributario(
            @Param("clienteId") Long clienteId,
            @Param("fechaInicio") LocalDate fechaInicio,
            @Param("fechaFin") LocalDate fechaFin);

    /**
     * Obtiene los últimos 5 ingresos de un cliente ordenados por fecha descendente
     */
    List<Ingreso> findTop5ByClienteIdOrderByFechaDesc(Long clienteId);
}
