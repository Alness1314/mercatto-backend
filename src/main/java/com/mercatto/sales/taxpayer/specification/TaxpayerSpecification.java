package com.mercatto.sales.taxpayer.specification;

import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import org.springframework.data.jpa.domain.Specification;

import com.mercatto.sales.taxpayer.entity.TaxpayerEntity;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

public class TaxpayerSpecification implements Specification<TaxpayerEntity> {
    @SuppressWarnings("null")
    @Override
    public Predicate toPredicate(Root<TaxpayerEntity> root, CriteriaQuery<?> query,
            CriteriaBuilder criteriaBuilder) {
        return null;
    }

    public Specification<TaxpayerEntity> getSpecificationByFilters(Map<String, String> parameters) {
        Specification<TaxpayerEntity> specification = this.filterByErased("false");
        for (Entry<String, String> entry : parameters.entrySet()) {
            switch (entry.getKey()) {
                case "id":
                    specification = specification.and(this.filterById(entry.getValue()));
                    break;
                case "rfc":
                    specification = specification.and(this.filterByRfc(entry.getValue()));
                    break;
                default:
                    break;
            }
        }
        return specification;
    }

    private Specification<TaxpayerEntity> filterById(String id) {
        return (root, query, cb) -> cb.equal(root.<UUID>get("id"), UUID.fromString(id));
    }

    private Specification<TaxpayerEntity> filterByRfc(String rfc) {
        return (root, query, cb) -> cb.equal(root.<String>get("rfc"), rfc);

    }

    private Specification<TaxpayerEntity> filterByErased(String erase) {
        return (root, query, cb) -> cb.equal(root.<Boolean>get("erased"), Boolean.parseBoolean(erase));
    }
}
