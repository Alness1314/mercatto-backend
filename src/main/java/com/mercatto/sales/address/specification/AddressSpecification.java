package com.mercatto.sales.address.specification;

import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import org.springframework.data.jpa.domain.Specification;

import com.mercatto.sales.address.entity.AddressEntity;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

public class AddressSpecification implements Specification<AddressEntity> {
    @SuppressWarnings("null")
    @Override
    public Predicate toPredicate(Root<AddressEntity> root, CriteriaQuery<?> query,
            CriteriaBuilder criteriaBuilder) {
        return null;
    }

    public Specification<AddressEntity> getSpecificationByFilters(Map<String, String> parameters) {
        Specification<AddressEntity> specification = this.filterByErased("false");
        for (Entry<String, String> entry : parameters.entrySet()) {
            switch (entry.getKey()) {
                case "id":
                    specification = specification.and(this.filterById(entry.getValue()));
                    break;
                case "nickname":
                    specification = specification.and(this.filterByNickname(entry.getValue()));
                    break;
                default:
                    break;
            }
        }
        return specification;
    }

    private Specification<AddressEntity> filterById(String id) {
        return (root, query, cb) -> cb.equal(root.<UUID>get("id"), UUID.fromString(id));
    }

    private Specification<AddressEntity> filterByNickname(String nickname) {
        return (root, query, cb) -> cb.equal(root.<String>get("nickname"), nickname);

    }

    private Specification<AddressEntity> filterByErased(String erase) {
        return (root, query, cb) -> cb.equal(root.<Boolean>get("erased"), Boolean.parseBoolean(erase));
    }
}
