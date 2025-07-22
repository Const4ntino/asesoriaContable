package com.asesoria.contable.app_ac.repository;

import com.asesoria.contable.app_ac.model.entity.AlertaCliente;
import com.asesoria.contable.app_ac.model.entity.AlertaContador;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AlertaContadorRepository extends JpaRepository<AlertaContador, Long> {
    List<AlertaContador> findByContadorIdOrderByFechaCreacionDesc(Long idContador);
}
