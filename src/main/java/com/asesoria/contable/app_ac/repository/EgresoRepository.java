package com.asesoria.contable.app_ac.repository;

import com.asesoria.contable.app_ac.model.entity.Egreso;
import com.asesoria.contable.app_ac.utils.enums.TipoTributario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface EgresoRepository extends JpaRepository<Egreso, Long> {
    List<Egreso> findByClienteId(Long clienteId);
    List<Egreso> findByClienteIdAndFechaBetween(Long clienteId, LocalDate startDate, LocalDate endDate);

    @Query("SELECT COALESCE(SUM(e.monto), 0) FROM Egreso e WHERE e.cliente.id = :clienteId AND e.fecha >= :startDate AND e.fecha <= :endDate")
    BigDecimal sumMontoByClienteIdAndFechaBetween(@Param("clienteId") Long clienteId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query("SELECT e.tipoContabilidad, COALESCE(SUM(e.monto), 0) FROM Egreso e WHERE e.cliente.id = :clienteId GROUP BY e.tipoContabilidad")
    List<Object[]> sumRawByTipoContabilidad(@Param("clienteId") Long clienteId);

    @Query("SELECT e.tipoTributario, COALESCE(SUM(e.monto), 0) FROM Egreso e WHERE e.cliente.id = :clienteId GROUP BY e.tipoTributario")
    List<Object[]> sumRawByTipoTributario(@Param("clienteId") Long clienteId);

    @Query("SELECT new map(e.descripcion as descripcion, e.monto as monto, e.fecha as fecha) FROM Egreso e WHERE e.cliente.id = :clienteId AND e.descripcion IN (SELECT e2.descripcion FROM Egreso e2 WHERE e2.cliente.id = :clienteId GROUP BY e2.descripcion HAVING COUNT(e2.descripcion) > 1)")
    List<Map<String, Object>> findEgresosRecurrentes(@Param("clienteId") Long clienteId);

    @Query("SELECT COALESCE(SUM(e.monto), 0) FROM Egreso e WHERE e.cliente.id = :clienteId AND e.tipoTributario = :tipoTributario AND e.fecha BETWEEN :startDate AND :endDate")
    BigDecimal sumMontoByClienteIdAndTipoTributarioAndFechaBetween(@Param("clienteId") Long clienteId, @Param("tipoTributario") TipoTributario tipoTributario, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query("SELECT COALESCE(SUM(e.montoIgv), 0) FROM Egreso e WHERE e.cliente.id = :clienteId AND e.fecha BETWEEN :startDate AND :endDate")
    BigDecimal sumMontoIgvByClienteIdAndFechaBetween(@Param("clienteId") Long clienteId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query("SELECT COALESCE(SUM(e.monto), 0) FROM Egreso e WHERE e.cliente.id = :clienteId AND FUNCTION('DATE_TRUNC', 'month', e.fecha) = FUNCTION('DATE_TRUNC', 'month', :periodo)")
    BigDecimal sumMontoByClienteIdAndPeriodo(@Param("clienteId") Long clienteId, @Param("periodo") LocalDate periodo);
}
