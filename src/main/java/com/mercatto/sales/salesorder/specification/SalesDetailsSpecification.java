package com.mercatto.sales.salesorder.specification;

import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.lang.Nullable;

import com.mercatto.sales.salesorder.entity.SalesDetailsEntity;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

public class SalesDetailsSpecification implements Specification<SalesDetailsEntity>{

    @SuppressWarnings("null")
    @Override
    @Nullable
    public Predicate toPredicate(Root<SalesDetailsEntity> arg0, @Nullable CriteriaQuery<?> arg1, CriteriaBuilder arg2) {
        return null;
    }

    public Specification<SalesDetailsEntity> getSpecificationByFilters(Map<String, String> parameters) {
        Specification<SalesDetailsEntity> specification = this.filterByErased("false");
        for (Entry<String, String> entry : parameters.entrySet()) {
            switch (entry.getKey()) {
                case "id":
                    specification = specification.and(this.filterById(entry.getValue()));
                    break;
                case "product":
                    specification = specification.and(this.filterByProduct(entry.getValue()));
                    break;
                case "sale":
                    specification = specification.and(this.filterBySales(entry.getValue()));
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

    private Specification<SalesDetailsEntity> filterById(String id) {
        return (root, query, cb) -> cb.equal(root.<UUID>get("id"), UUID.fromString(id));
    }

    private Specification<SalesDetailsEntity> filterByProduct(String productId) {
        return (root, query, cb) -> cb.equal(root.get("product").get("id"), UUID.fromString(productId));
    }

    private Specification<SalesDetailsEntity> filterBySales(String salesId) {
        return (root, query, cb) -> cb.equal(root.get("sales").get("id"), UUID.fromString(salesId));
    }

    private Specification<SalesDetailsEntity> filterByErased(String erase) {
        return (root, query, cb) -> cb.equal(root.<Boolean>get("erased"), Boolean.parseBoolean(erase));
    }

    private Specification<SalesDetailsEntity> filterByCompanyId(String companyId) {
        return (root, query, cb) -> cb.equal(root.<UUID>get("company"), UUID.fromString(companyId));
    }
    
}
