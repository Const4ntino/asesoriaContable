package com.asesoria.contable.app_ac.repository;

import com.asesoria.contable.app_ac.model.entity.Egreso;
import com.asesoria.contable.app_ac.model.entity.Ingreso;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

public interface EgresoRepository extends JpaRepository<Egreso, Long> {
    Collection<Egreso> findByClienteId(Long clienteId);
    List<Egreso> findByClienteIdAndFechaBetween(Long clienteId, LocalDate startDate, LocalDate endDate);
}
