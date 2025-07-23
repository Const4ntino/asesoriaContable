package com.asesoria.contable.app_ac.repository;

import com.asesoria.contable.app_ac.model.entity.Pago;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

/**
 * Interfaz para métodos personalizados del repositorio de Pagos
 */
public interface PagoRepositoryCustom {

    /**
     * Busca pagos asociados a obligaciones de un cliente en un rango de fechas
     */
    @Query("SELECT p FROM Pago p WHERE p.obligacion.cliente.id = :clienteId AND p.obligacion.periodoTributario BETWEEN :fechaInicio AND :fechaFin ORDER BY p.fechaPago DESC")
    List<Pago> findByObligacionClienteIdAndObligacionPeriodoTributarioBetween(
            @Param("clienteId") Long clienteId,
            @Param("fechaInicio") LocalDate fechaInicio,
            @Param("fechaFin") LocalDate fechaFin);
}
