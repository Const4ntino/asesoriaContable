package com.asesoria.contable.app_ac.specification;

import com.asesoria.contable.app_ac.model.entity.Cliente;
import com.asesoria.contable.app_ac.model.entity.Contador;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class ContadorSpecification {

    public static Specification<Contador> contadoresConClientes(String search) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            
            // Usamos un subquery para verificar que el contador tenga al menos un cliente
            // Verificamos que query no sea null antes de usarlo
            if (query != null) {
                var subquery = query.subquery(Long.class);
                var clienteRoot = subquery.from(Cliente.class);
                subquery.select(clienteRoot.get("contador").get("id"));
                subquery.where(criteriaBuilder.equal(clienteRoot.get("contador"), root));
                
                // Agregamos la condición de que el contador debe tener al menos un cliente
                predicates.add(criteriaBuilder.exists(subquery));
            }
            
            // La condición ya fue agregada en el bloque anterior
            
            // Filtro de búsqueda por nombre, apellidos o DNI
            if (StringUtils.hasText(search)) {
                String lowerCaseSearch = search.toLowerCase();
                predicates.add(criteriaBuilder.or(
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("nombres")), "%" + lowerCaseSearch + "%"),
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("apellidos")), "%" + lowerCaseSearch + "%"),
                        criteriaBuilder.like(root.get("dni"), "%" + search + "%")
                ));
            }
            
            // Eliminar duplicados
            if (query != null) {
                query.distinct(true);
            }
            
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
