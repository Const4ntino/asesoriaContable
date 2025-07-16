package com.asesoria.contable.app_ac.repository;

import com.asesoria.contable.app_ac.model.entity.Cliente;
import com.asesoria.contable.app_ac.model.entity.ObligacionNrus;
import com.asesoria.contable.app_ac.utils.enums.EstadoObligacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ObligacionNrusRepository extends JpaRepository<ObligacionNrus, Long> {
    Optional<ObligacionNrus> findByClienteAndPeriodoTributario(Cliente cliente, LocalDate periodoTributario);

    @Query("SELECT o FROM ObligacionNrus o WHERE o.cliente.id = :clienteId " +
           "AND (:periodoTributario IS NULL OR o.periodoTributario = :periodoTributario) " +
           "AND (:estado IS NULL OR o.estado = :estado)")
    List<ObligacionNrus> findByClienteIdAndOptionalPeriodoAndEstado(
            @Param("clienteId") Long clienteId,
            @Param("periodoTributario") LocalDate periodoTributario,
            @Param("estado") EstadoObligacion estado);
}
