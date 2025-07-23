package com.asesoria.contable.app_ac.repository.impl;

import com.asesoria.contable.app_ac.model.entity.Obligacion;
import com.asesoria.contable.app_ac.repository.ObligacionRepositoryCustom;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

import java.time.LocalDate;
import java.util.List;

/**
 * Implementación de los métodos personalizados para el repositorio de Obligaciones
 */
public class ObligacionRepositoryCustomImpl implements ObligacionRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Obligacion> findByClienteIdAndPeriodoTributarioBetween(Long clienteId, LocalDate fechaInicio, LocalDate fechaFin) {
        Query query = entityManager.createQuery(
                "SELECT o FROM Obligacion o WHERE o.cliente.id = :clienteId AND o.periodoTributario BETWEEN :fechaInicio AND :fechaFin ORDER BY o.fechaLimite ASC"
        );
        query.setParameter("clienteId", clienteId);
        query.setParameter("fechaInicio", fechaInicio);
        query.setParameter("fechaFin", fechaFin);
        
        return query.getResultList();
    }
}
