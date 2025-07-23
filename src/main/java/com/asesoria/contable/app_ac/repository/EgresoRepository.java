package com.asesoria.contable.app_ac.repository;

import com.asesoria.contable.app_ac.model.entity.Egreso;
import com.asesoria.contable.app_ac.utils.enums.TipoTributario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface EgresoRepository extends JpaRepository<Egreso, Long>, EgresoRepositoryCustom {
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

    @Query("SELECT COALESCE(SUM(e.monto), 0) FROM Egreso e WHERE e.cliente.id = :clienteId AND FUNCTION('DATE_TRUNC', 'month', e.fecha) = FUNCTION('DATE_TRUNC', 'month', CAST(:periodo AS java.sql.Date))")
    BigDecimal sumMontoByClienteIdAndPeriodo(@Param("clienteId") Long clienteId, @Param("periodo") LocalDate periodo);
    
    // Método para filtrar egresos con múltiples criterios y paginación
    @Query("SELECT e FROM Egreso e WHERE e.cliente.id = :clienteId " +
            "AND (:montoMinimo IS NULL OR e.monto >= :montoMinimo) " +
            "AND (:montoMaximo IS NULL OR e.monto <= :montoMaximo) " +
            "AND (:fechaInicio IS NULL OR e.fecha >= :fechaInicio) " +
            "AND (:fechaFin IS NULL OR e.fecha <= :fechaFin) " +
            "AND (:mes IS NULL OR EXTRACT(MONTH FROM e.fecha) = :mes) " +
            "AND (:anio IS NULL OR EXTRACT(YEAR FROM e.fecha) = :anio) " +
            "AND (:tipoTributario IS NULL OR e.tipoTributario = :tipoTributario) " +
            "AND (:descripcion IS NULL OR :descripcion = '' OR e.descripcion LIKE CONCAT('%', :descripcion, '%')) " +
            "AND (:nroComprobante IS NULL OR :nroComprobante = '' OR e.nroComprobante LIKE CONCAT('%', :nroComprobante, '%'))")

    Page<Egreso> filtrarEgresos(
            @Param("clienteId") Long clienteId,
            @Param("montoMinimo") BigDecimal montoMinimo,
            @Param("montoMaximo") BigDecimal montoMaximo,
            @Param("fechaInicio") LocalDate fechaInicio,
            @Param("fechaFin") LocalDate fechaFin,
            @Param("mes") Integer mes,
            @Param("anio") Integer anio,
            @Param("tipoTributario") TipoTributario tipoTributario,
            @Param("descripcion") String descripcion,
            @Param("nroComprobante") String nroComprobante,
            Pageable pageable);

}
