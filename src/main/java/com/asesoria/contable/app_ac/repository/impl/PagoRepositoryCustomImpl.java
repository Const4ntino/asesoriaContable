package com.asesoria.contable.app_ac.repository.impl;

import com.asesoria.contable.app_ac.model.entity.Pago;
import com.asesoria.contable.app_ac.repository.PagoRepositoryCustom;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

import java.time.LocalDate;
import java.util.List;

/**
 * Implementación de los métodos personalizados para el repositorio de Pagos
 */
public class PagoRepositoryCustomImpl implements PagoRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Pago> findByObligacionClienteIdAndObligacionPeriodoTributarioBetween(Long clienteId, LocalDate fechaInicio, LocalDate fechaFin) {
        Query query = entityManager.createQuery(
                "SELECT p FROM Pago p WHERE p.obligacion.cliente.id = :clienteId AND p.obligacion.periodoTributario BETWEEN :fechaInicio AND :fechaFin ORDER BY p.fechaPago DESC"
        );
        query.setParameter("clienteId", clienteId);
        query.setParameter("fechaInicio", fechaInicio);
        query.setParameter("fechaFin", fechaFin);
        
        return query.getResultList();
    }
}
