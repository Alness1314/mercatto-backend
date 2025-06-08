package com.mercatto.sales.transactions.specification;

import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.lang.Nullable;

import com.mercatto.sales.transactions.entity.SalesEntity;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

public class SalesSpecification implements Specification<SalesEntity> {

    @SuppressWarnings("null")
    @Override
    @Nullable
    public Predicate toPredicate(Root<SalesEntity> arg0, @Nullable CriteriaQuery<?> arg1, CriteriaBuilder arg2) {
        return null;
    }

    public Specification<SalesEntity> getSpecificationByFilters(Map<String, String> parameters) {
        Specification<SalesEntity> specification = this.filterByErased("false");
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
                case "user":
                    specification = specification.and(this.filterByUserId(entry.getValue()));
                    break;
                default:
                    break;
            }
        }
        return specification;
    }

    private Specification<SalesEntity> filterById(String id) {
        return (root, query, cb) -> cb.equal(root.<UUID>get("id"), UUID.fromString(id));
    }

    private Specification<SalesEntity> filterByName(String name) {
        return (root, query, cb) -> cb.equal(root.<String>get("name"), name);
    }

    private Specification<SalesEntity> filterByErased(String erase) {
        return (root, query, cb) -> cb.equal(root.<Boolean>get("erased"), Boolean.parseBoolean(erase));
    }

    private Specification<SalesEntity> filterByCompanyId(String companyId) {
        return (root, query, cb) -> cb.equal(root.get("company").get("id"), UUID.fromString(companyId));
    }

    private Specification<SalesEntity> filterByUserId(String userId) {
        return (root, query, cb) -> cb.equal(root.get("user").get("id"), UUID.fromString(userId));
    }

}
