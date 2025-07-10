package com.asesoria.contable.app_ac.repository;

import com.asesoria.contable.app_ac.model.entity.Pago;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PagoRepository extends JpaRepository<Pago, Long> {
}
