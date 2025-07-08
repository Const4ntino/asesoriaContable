package com.asesoria.contable.app_ac.repository;

import com.asesoria.contable.app_ac.model.entity.Ingreso;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface IngresoRepository extends JpaRepository<Ingreso, Long> {
    List<Ingreso> findByClienteId(Long clienteId);
    List<Ingreso> findByClienteIdAndFechaBetween(Long clienteId, LocalDate startDate, LocalDate endDate);

    @Query("SELECT COALESCE(SUM(i.monto), 0) FROM Ingreso i WHERE i.cliente.id = :clienteId AND i.fecha >= :startDate AND i.fecha <= :endDate")
    BigDecimal sumMontoByClienteIdAndFechaBetween(@Param("clienteId") Long clienteId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query("SELECT new map(i.categoria as categoria, COALESCE(SUM(i.monto), 0) as total) FROM Ingreso i WHERE i.cliente.id = :clienteId GROUP BY i.categoria")
    List<Map<String, Object>> sumMontoByCategoria(@Param("clienteId") Long clienteId);

    @Query("SELECT COALESCE(SUM(i.monto), 0) FROM Ingreso i WHERE i.cliente.id = :clienteId AND i.estadoPago = 'PENDIENTE'")
    BigDecimal sumMontoByClienteIdAndEstadoPagoPendiente(@Param("clienteId") Long clienteId);

    @Query("SELECT COUNT(i) FROM Ingreso i WHERE i.cliente.id = :clienteId AND i.fecha >= :startDate AND i.fecha <= :endDate")
    Long countByClienteIdAndFechaBetween(@Param("clienteId") Long clienteId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
}
