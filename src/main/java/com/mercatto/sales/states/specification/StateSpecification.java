package com.mercatto.sales.states.specification;

import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import org.springframework.data.jpa.domain.Specification;

import com.mercatto.sales.states.entity.StateEntity;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

public class StateSpecification implements Specification<StateEntity> {
    @SuppressWarnings("null")
    @Override
    public Predicate toPredicate(Root<StateEntity> root, CriteriaQuery<?> query,
            CriteriaBuilder criteriaBuilder) {
        return null;
    }

    public Specification<StateEntity> getSpecificationByFilters(Map<String, String> parameters) {
        Specification<StateEntity> specification = this.filterByErased("false");
        for (Entry<String, String> entry : parameters.entrySet()) {
            switch (entry.getKey()) {
                case "id":
                    specification = specification.and(this.filterById(entry.getValue()));
                    break;
                case "name":
                    specification = specification.and(this.filterByName(entry.getValue()));
                    break;
                case "country":
                    specification = specification.and(this.filterByCountry(entry.getValue()));
                    break;
                default:
                    break;
            }
        }
        return specification;
    }

    private Specification<StateEntity> filterById(String id) {
        return (root, query, cb) -> cb.equal(root.<UUID>get("id"), UUID.fromString(id));
    }

    private Specification<StateEntity> filterByName(String name) {
        return (root, query, cb) -> cb.equal(root.<String>get("name"), name);

    }

    private Specification<StateEntity> filterByErased(String erase) {
        return (root, query, cb) -> cb.equal(root.<Boolean>get("erased"), Boolean.parseBoolean(erase));
    }

    private Specification<StateEntity> filterByCountry(String countryId) {
        return (root, query, cb) -> cb.equal(root.get("country").<UUID>get("id"), UUID.fromString(countryId));
    }
}
