package com.asesoria.contable.app_ac.repository.impl;

import com.asesoria.contable.app_ac.model.entity.Declaracion;
import com.asesoria.contable.app_ac.repository.DeclaracionRepositoryCustom;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

import java.time.LocalDate;
import java.util.List;

/**
 * Implementación de los métodos personalizados para el repositorio de Declaraciones
 */
public class DeclaracionRepositoryCustomImpl implements DeclaracionRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Declaracion> findByClienteIdAndPeriodoTributarioBetween(Long clienteId, LocalDate fechaInicio, LocalDate fechaFin) {
        Query query = entityManager.createQuery(
                "SELECT d FROM Declaracion d WHERE d.cliente.id = :clienteId AND d.periodoTributario BETWEEN :fechaInicio AND :fechaFin ORDER BY d.periodoTributario DESC"
        );
        query.setParameter("clienteId", clienteId);
        query.setParameter("fechaInicio", fechaInicio);
        query.setParameter("fechaFin", fechaFin);
        
        return query.getResultList();
    }
}
