package com.asesoria.contable.app_ac.repository;

import com.asesoria.contable.app_ac.model.entity.Obligacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ObligacionRepository extends JpaRepository<Obligacion, Long> {
    List<Obligacion> findByClienteId(Long clienteId);
}
