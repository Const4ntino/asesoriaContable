package com.asesoria.contable.app_ac.repository;

import com.asesoria.contable.app_ac.model.entity.PagoNrus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PagoNrusRepository extends JpaRepository<PagoNrus, Long> {
}
