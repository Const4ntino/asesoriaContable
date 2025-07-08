package com.asesoria.contable.app_ac.repository;

import com.asesoria.contable.app_ac.model.entity.Declaracion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface DeclaracionRepository extends JpaRepository<Declaracion, Long> {
    List<Declaracion> findByClienteId(Long clienteId);
}
