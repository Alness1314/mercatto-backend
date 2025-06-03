package com.mercatto.sales.country.specification;

import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import org.springframework.data.jpa.domain.Specification;

import com.mercatto.sales.country.entity.CountryEntity;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

public class CountrySpecification implements Specification<CountryEntity> {
    @Override
    public Predicate toPredicate(Root<CountryEntity> arg0, CriteriaQuery<?> arg1, CriteriaBuilder arg2) {
        return null;
    }

    public Specification<CountryEntity> getSpecificationByFilters(Map<String, String> parameters) {
        Specification<CountryEntity> specification = this.filterByErased("false");
        for (Entry<String, String> entry : parameters.entrySet()) {
            switch (entry.getKey()) {
                case "id":
                    specification = specification.and(this.filterById(entry.getValue()));
                    break;
                case "name":
                    specification = specification.and(this.filterByName(entry.getValue()));
                    break;
                default:
                    break;
            }
        }
        return specification;
    }

    private Specification<CountryEntity> filterById(String id) {
        return (root, query, cb) -> cb.equal(root.<UUID>get("id"), UUID.fromString(id));
    }

    private Specification<CountryEntity> filterByName(String name) {
        return (root, query, cb) -> cb.equal(root.<String>get("name"), name);

    }

    private Specification<CountryEntity> filterByErased(String erase) {
        return (root, query, cb) -> cb.equal(root.<Boolean>get("erased"), Boolean.parseBoolean(erase));
    }

}
