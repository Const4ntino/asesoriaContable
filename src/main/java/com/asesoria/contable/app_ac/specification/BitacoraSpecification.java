package com.asesoria.contable.app_ac.specification;

import com.asesoria.contable.app_ac.model.entity.BitacoraMovimiento;
import com.asesoria.contable.app_ac.utils.enums.Accion;
import com.asesoria.contable.app_ac.utils.enums.Modulo;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class BitacoraSpecification {

    public static Specification<BitacoraMovimiento> filtrarBitacora(
            Long usuarioId,
            String searchTerm,
            Modulo modulo,
            Accion accion,
            LocalDate fechaDesde,
            LocalDate fechaHasta) {

        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (usuarioId != null) {
                predicates.add(criteriaBuilder.equal(root.get("usuario").get("id"), usuarioId));
            }

            if (StringUtils.hasText(searchTerm)) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("usuario").get("username")), "%" + searchTerm.toLowerCase() + "%"));
            }

            if (modulo != null) {
                predicates.add(criteriaBuilder.equal(root.get("modulo"), modulo));
            }

            if (accion != null) {
                predicates.add(criteriaBuilder.equal(root.get("accion"), accion));
            }

            if (fechaDesde != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("fechaMovimiento"), fechaDesde.atStartOfDay()));
            }

            if (fechaHasta != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("fechaMovimiento"), fechaHasta.atTime(23, 59, 59)));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}