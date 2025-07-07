package com.asesoria.contable.app_ac.repository;

import com.asesoria.contable.app_ac.model.entity.Ingreso;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IngresoRepository extends JpaRepository<Ingreso, Long> {
}
