package com.asesoria.contable.app_ac.repository;

import com.asesoria.contable.app_ac.model.entity.Ingreso;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface IngresoRepository extends JpaRepository<Ingreso, Long> {
    List<Ingreso> findByClienteId(Long clienteId);
    List<Ingreso> findByClienteIdAndFechaBetween(Long clienteId, LocalDate startDate, LocalDate endDate);
}
