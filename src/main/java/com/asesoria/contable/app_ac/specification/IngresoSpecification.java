package com.asesoria.contable.app_ac.specification;

import com.asesoria.contable.app_ac.model.entity.Ingreso;
import com.asesoria.contable.app_ac.utils.enums.TipoTributario;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

public class IngresoSpecification {

    public static Specification<Ingreso> filtrarIngresos(
            Long clienteId,
            BigDecimal montoMinimo,
            BigDecimal montoMaximo,
            LocalDate fechaInicio,
            LocalDate fechaFin,
            Integer mes,
            Integer anio,
            TipoTributario tipoTributario,
            String descripcion,
            String nroComprobante) {

        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Filtro por cliente (obligatorio)
            if (clienteId != null) {
                predicates.add(criteriaBuilder.equal(root.get("cliente").get("id"), clienteId));
            }

            // Filtro por monto mínimo
            if (montoMinimo != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("monto"), montoMinimo));
            }

            // Filtro por monto máximo
            if (montoMaximo != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("monto"), montoMaximo));
            }

            // Filtro por rango de fechas
            if (fechaInicio != null && fechaFin != null) {
                predicates.add(criteriaBuilder.between(root.get("fecha"), fechaInicio, fechaFin));
            } else if (fechaInicio != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("fecha"), fechaInicio));
            } else if (fechaFin != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("fecha"), fechaFin));
            }

            // Filtro por mes y año (periodo)
            if (mes != null && anio != null) {
                LocalDate inicioMes = LocalDate.of(anio, mes, 1);
                LocalDate finMes = YearMonth.of(anio, mes).atEndOfMonth();
                predicates.add(criteriaBuilder.between(root.get("fecha"), inicioMes, finMes));
            } else if (mes != null) {
                // Si solo se proporciona el mes, asumimos el año actual (2025)
                LocalDate inicioMes = LocalDate.of(2025, mes, 1);
                LocalDate finMes = YearMonth.of(2025, mes).atEndOfMonth();
                predicates.add(criteriaBuilder.between(root.get("fecha"), inicioMes, finMes));
            } else if (anio != null) {
                // Si solo se proporciona el año
                LocalDate inicioAnio = LocalDate.of(anio, 1, 1);
                LocalDate finAnio = LocalDate.of(anio, 12, 31);
                predicates.add(criteriaBuilder.between(root.get("fecha"), inicioAnio, finAnio));
            }

            // Filtro por tipo tributario
            if (tipoTributario != null) {
                predicates.add(criteriaBuilder.equal(root.get("tipoTributario"), tipoTributario));
            }

            // Filtro por descripción
            if (StringUtils.hasText(descripcion)) {
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("descripcion")),
                        "%" + descripcion.toLowerCase() + "%"));
            }

            // Filtro por número de comprobante
            if (StringUtils.hasText(nroComprobante)) {
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("nroComprobante")),
                        "%" + nroComprobante.toLowerCase() + "%"));
            }

            // Eliminar duplicados
            if (query != null) {
                query.distinct(true);
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
