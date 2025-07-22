package com.asesoria.contable.app_ac.repository;

import com.asesoria.contable.app_ac.model.entity.BitacoraMovimiento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface BitacoraRepository extends JpaRepository<BitacoraMovimiento, Long>, JpaSpecificationExecutor<BitacoraMovimiento> {
}
