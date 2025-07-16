package com.asesoria.contable.app_ac.repository;

import com.asesoria.contable.app_ac.model.entity.ObligacionNrus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ObligacionNrusRepository extends JpaRepository<ObligacionNrus, Long> {
}
