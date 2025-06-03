package com.mercatto.sales.cities.specification;

import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import org.springframework.data.jpa.domain.Specification;

import com.mercatto.sales.cities.entity.CityEntity;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

public class CitySpecification implements Specification<CityEntity> {
    @Override
    public Predicate toPredicate(Root<CityEntity> root, CriteriaQuery<?> query,
            CriteriaBuilder criteriaBuilder) {
        return null;
    }

    public Specification<CityEntity> getSpecificationByFilters(Map<String, String> parameters) {
        Specification<CityEntity> specification = this.filterByErased("false");
        for (Entry<String, String> entry : parameters.entrySet()) {
            switch (entry.getKey()) {
                case "id":
                    specification = specification.and(this.filterById(entry.getValue()));
                    break;
                case "name":
                    specification = specification.and(this.filterByName(entry.getValue()));
                    break;
                case "state":
                    specification = specification.and(this.filterByState(entry.getValue()));
                    break;
                default:
                    break;
            }
        }
        return specification;
    }

    private Specification<CityEntity> filterById(String id) {
        return (root, query, cb) -> cb.equal(root.<UUID>get("id"), UUID.fromString(id));
    }

    private Specification<CityEntity> filterByName(String name) {
        return (root, query, cb) -> cb.equal(root.<String>get("name"), name);

    }

    private Specification<CityEntity> filterByErased(String erase) {
        return (root, query, cb) -> cb.equal(root.<Boolean>get("erased"), Boolean.parseBoolean(erase));
    }

    private Specification<CityEntity> filterByState(String stateId) {
        return (root, query, cb) -> cb.equal(root.get("state").<UUID>get("id"), UUID.fromString(stateId));
    }
}
