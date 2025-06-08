package com.mercatto.sales.unit.specification;

import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import org.springframework.data.jpa.domain.Specification;

import com.mercatto.sales.unit.entity.UnitMeasurement;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

public class UnitMeasurementSpec implements Specification<UnitMeasurement> {
    @SuppressWarnings("null")
    @Override
    public Predicate toPredicate(Root<UnitMeasurement> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        return null;
    }

    public Specification<UnitMeasurement> getSpecificationByFilters(Map<String, String> parameters) {
        Specification<UnitMeasurement> specification = this.filterByErased("false");
        for (Entry<String, String> entry : parameters.entrySet()) {
            switch (entry.getKey()) {
                case "id":
                    specification = specification.and(this.filterById(entry.getValue()));
                    break;
                case "name":
                    specification = specification.and(this.filterByName(entry.getValue()));
                    break;
                case "company":
                    specification = specification.and(this.filterByCompanyId(entry.getValue()));
                    break;
                default:
                    break;
            }
        }
        return specification;
    }

    private Specification<UnitMeasurement> filterById(String id) {
        return (root, query, cb) -> cb.equal(root.<UUID>get("id"), UUID.fromString(id));
    }

    private Specification<UnitMeasurement> filterByName(String name) {
        return (root, query, cb) -> cb.equal(root.<String>get("name"), name);
    }

    private Specification<UnitMeasurement> filterByErased(String erase) {
        return (root, query, cb) -> cb.equal(root.<Boolean>get("erased"), Boolean.parseBoolean(erase));
    }

    private Specification<UnitMeasurement> filterByCompanyId(String companyId) {
        return (root, query, cb) -> cb.equal(root.get("company").get("id"), UUID.fromString(companyId));
    }
}
