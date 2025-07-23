package com.asesoria.contable.app_ac.repository.impl;

import com.asesoria.contable.app_ac.model.entity.Egreso;
import com.asesoria.contable.app_ac.repository.EgresoRepositoryCustom;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Implementación de los métodos personalizados para el repositorio de Egresos
 */
public class EgresoRepositoryCustomImpl implements EgresoRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public BigDecimal sumMontoByClienteIdAndFechaBetween(Long clienteId, LocalDate fechaInicio, LocalDate fechaFin) {
        Query query = entityManager.createQuery(
                "SELECT COALESCE(SUM(e.monto), 0) FROM Egreso e WHERE e.cliente.id = :clienteId AND e.fecha BETWEEN :fechaInicio AND :fechaFin"
        );
        query.setParameter("clienteId", clienteId);
        query.setParameter("fechaInicio", fechaInicio);
        query.setParameter("fechaFin", fechaFin);
        
        BigDecimal result = (BigDecimal) query.getSingleResult();
        return result != null ? result : BigDecimal.ZERO;
    }

    @Override
    public BigDecimal sumMontoIgvByClienteIdAndFechaBetween(Long clienteId, LocalDate fechaInicio, LocalDate fechaFin) {
        Query query = entityManager.createQuery(
                "SELECT COALESCE(SUM(e.montoIgv), 0) FROM Egreso e WHERE e.cliente.id = :clienteId AND e.fecha BETWEEN :fechaInicio AND :fechaFin"
        );
        query.setParameter("clienteId", clienteId);
        query.setParameter("fechaInicio", fechaInicio);
        query.setParameter("fechaFin", fechaFin);
        
        BigDecimal result = (BigDecimal) query.getSingleResult();
        return result != null ? result : BigDecimal.ZERO;
    }

    @Override
    public List<Object[]> findDistribucionPorTipoTributario(Long clienteId, LocalDate fechaInicio, LocalDate fechaFin) {
        Query query = entityManager.createQuery(
                "SELECT e.tipoTributario, SUM(e.monto) FROM Egreso e " +
                "WHERE e.cliente.id = :clienteId AND e.fecha BETWEEN :fechaInicio AND :fechaFin " +
                "GROUP BY e.tipoTributario"
        );
        query.setParameter("clienteId", clienteId);
        query.setParameter("fechaInicio", fechaInicio);
        query.setParameter("fechaFin", fechaFin);
        
        return query.getResultList();
    }

    @Override
    public List<Object[]> findDistribucionPorTipoContabilidad(Long clienteId, LocalDate fechaInicio, LocalDate fechaFin) {
        Query query = entityManager.createQuery(
                "SELECT e.tipoContabilidad, SUM(e.monto) FROM Egreso e " +
                "WHERE e.cliente.id = :clienteId AND e.fecha BETWEEN :fechaInicio AND :fechaFin " +
                "GROUP BY e.tipoContabilidad"
        );
        query.setParameter("clienteId", clienteId);
        query.setParameter("fechaInicio", fechaInicio);
        query.setParameter("fechaFin", fechaFin);
        
        return query.getResultList();
    }

    @Override
    public List<Egreso> findTop5ByClienteIdOrderByFechaDesc(Long clienteId) {
        Query query = entityManager.createQuery(
                "SELECT e FROM Egreso e WHERE e.cliente.id = :clienteId ORDER BY e.fecha DESC"
        );
        query.setParameter("clienteId", clienteId);
        query.setMaxResults(5);
        return query.getResultList();
    }
}
