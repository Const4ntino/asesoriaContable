package com.asesoria.contable.app_ac.repository.impl;

import com.asesoria.contable.app_ac.model.entity.Ingreso;
import com.asesoria.contable.app_ac.repository.IngresoRepositoryCustom;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Implementación de los métodos personalizados para el repositorio de Ingresos
 */
public class IngresoRepositoryCustomImpl implements IngresoRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Ingreso> findTop5ByClienteIdOrderByFechaDesc(Long clienteId) {
        Query query = entityManager.createQuery(
                "SELECT i FROM Ingreso i WHERE i.cliente.id = :clienteId ORDER BY i.fecha DESC"
        );
        query.setParameter("clienteId", clienteId);
        query.setMaxResults(5);
        return query.getResultList();
    }

    @Override
    public BigDecimal sumMontoByClienteIdAndFechaBetween(Long clienteId, LocalDate fechaInicio, LocalDate fechaFin) {
        Query query = entityManager.createQuery(
                "SELECT COALESCE(SUM(i.monto), 0) FROM Ingreso i WHERE i.cliente.id = :clienteId AND i.fecha BETWEEN :fechaInicio AND :fechaFin"
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
                "SELECT COALESCE(SUM(i.montoIgv), 0) FROM Ingreso i WHERE i.cliente.id = :clienteId AND i.fecha BETWEEN :fechaInicio AND :fechaFin"
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
                "SELECT i.tipoTributario, SUM(i.monto) FROM Ingreso i " +
                "WHERE i.cliente.id = :clienteId AND i.fecha BETWEEN :fechaInicio AND :fechaFin " +
                "GROUP BY i.tipoTributario"
        );
        query.setParameter("clienteId", clienteId);
        query.setParameter("fechaInicio", fechaInicio);
        query.setParameter("fechaFin", fechaFin);
        
        return query.getResultList();
    }
}
