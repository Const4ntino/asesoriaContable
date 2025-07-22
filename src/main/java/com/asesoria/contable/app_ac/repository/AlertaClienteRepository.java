package com.asesoria.contable.app_ac.repository;

import com.asesoria.contable.app_ac.model.entity.AlertaCliente;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AlertaClienteRepository extends JpaRepository<AlertaCliente, Long> {
    List<AlertaCliente> findByClienteIdOrderByFechaCreacionDesc(Long idCliente);
}
