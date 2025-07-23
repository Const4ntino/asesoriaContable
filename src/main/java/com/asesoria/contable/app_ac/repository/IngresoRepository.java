package com.asesoria.contable.app_ac.repository;

import com.asesoria.contable.app_ac.model.entity.Ingreso;
import com.asesoria.contable.app_ac.utils.enums.TipoTributario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface IngresoRepository extends JpaRepository<Ingreso, Long>, JpaSpecificationExecutor<Ingreso>, IngresoRepositoryCustom {
    List<Ingreso> findByClienteId(Long clienteId);
    List<Ingreso> findByClienteIdAndFechaBetween(Long clienteId, LocalDate startDate, LocalDate endDate);

    @Query("SELECT COALESCE(SUM(i.monto), 0) FROM Ingreso i WHERE i.cliente.id = :clienteId AND i.fecha >= :startDate AND i.fecha <= :endDate")
    BigDecimal sumMontoByClienteIdAndFechaBetween(@Param("clienteId") Long clienteId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query("SELECT new map(i.tipoTributario as categoria, COALESCE(SUM(i.monto), 0) as total) FROM Ingreso i WHERE i.cliente.id = :clienteId GROUP BY i.tipoTributario")
    List<Map<String, Object>> sumMontoByCategoria(@Param("clienteId") Long clienteId);

    @Query("SELECT i.tipoTributario, COALESCE(SUM(i.monto), 0) FROM Ingreso i WHERE i.cliente.id = :clienteId GROUP BY i.tipoTributario")
    List<Object[]> sumRawByTipoTributario(@Param("clienteId") Long clienteId);

    @Query("SELECT new map(i.descripcion as descripcion, i.monto as monto, i.fecha as fecha) FROM Ingreso i WHERE i.cliente.id = :clienteId AND i.descripcion IN (SELECT i2.descripcion FROM Ingreso i2 WHERE i2.cliente.id = :clienteId GROUP BY i2.descripcion HAVING COUNT(i2.descripcion) > 1)")
    List<Map<String, Object>> findIngresosRecurrentes(@Param("clienteId") Long clienteId);

    @Query("SELECT COUNT(i) FROM Ingreso i WHERE i.cliente.id = :clienteId AND i.fecha >= :startDate AND i.fecha <= :endDate")
    Long countByClienteIdAndFechaBetween(@Param("clienteId") Long clienteId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query("SELECT COALESCE(SUM(i.monto), 0) FROM Ingreso i WHERE i.cliente.id = :clienteId AND i.tipoTributario = :tipoTributario AND i.fecha BETWEEN :startDate AND :endDate")
    BigDecimal sumMontoByClienteIdAndTipoTributarioAndFechaBetween(@Param("clienteId") Long clienteId, @Param("tipoTributario") TipoTributario tipoTributario, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query("SELECT COALESCE(SUM(i.montoIgv), 0) FROM Ingreso i WHERE i.cliente.id = :clienteId AND i.fecha BETWEEN :startDate AND :endDate")
    BigDecimal sumMontoIgvByClienteIdAndFechaBetween(@Param("clienteId") Long clienteId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query("SELECT COALESCE(SUM(i.monto), 0) FROM Ingreso i WHERE i.cliente.id = :clienteId AND FUNCTION('DATE_TRUNC', 'month', i.fecha) = FUNCTION('DATE_TRUNC', 'month', CAST(:periodo AS java.sql.Date))")
    BigDecimal sumMontoByClienteIdAndPeriodo(@Param("clienteId") Long clienteId, @Param("periodo") LocalDate periodo);
}
