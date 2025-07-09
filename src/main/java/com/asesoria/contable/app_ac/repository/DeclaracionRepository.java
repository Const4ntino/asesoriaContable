package com.asesoria.contable.app_ac.repository;

import com.asesoria.contable.app_ac.model.entity.Declaracion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface DeclaracionRepository extends JpaRepository<Declaracion, Long>, JpaSpecificationExecutor<Declaracion> {
    List<Declaracion> findByClienteId(Long clienteId);

    boolean existsByClienteIdAndPeriodoTributarioAndTipo(Long clienteId, LocalDate periodoTributario, String tipo);

    Optional<Declaracion> findByClienteIdAndPeriodoTributarioAndTipo(Long clienteId, LocalDate periodoTributario, String tipo);
}

