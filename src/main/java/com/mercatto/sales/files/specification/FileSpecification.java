package com.mercatto.sales.files.specification;

import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.lang.Nullable;

import com.mercatto.sales.files.entity.FileEntity;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

public class FileSpecification implements Specification<FileEntity>{

    @SuppressWarnings("null")
    @Override
    @Nullable
    public Predicate toPredicate(Root<FileEntity> arg0, @Nullable CriteriaQuery<?> arg1, CriteriaBuilder arg2) {
        return null;
    }

    public Specification<FileEntity> getSpecificationByFilters(Map<String, String> parameters) {
        Specification<FileEntity> specification = this.filterByErased("false");
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

    private Specification<FileEntity> filterById(String id) {
        return (root, query, cb) -> cb.equal(root.<UUID>get("id"), UUID.fromString(id));
    }

    private Specification<FileEntity> filterByName(String name) {
        return (root, query, cb) -> cb.equal(root.<String>get("name"), name);
    }

    private Specification<FileEntity> filterByErased(String erase) {
        return (root, query, cb) -> cb.equal(root.<Boolean>get("erased"), Boolean.parseBoolean(erase));
    }
    
}
