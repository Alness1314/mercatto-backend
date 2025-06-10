package com.mercatto.sales.permissions.specification;

import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.lang.Nullable;

import com.mercatto.sales.permissions.entity.PermissionEntity;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

public class PermissionSpecification implements Specification<PermissionEntity> {

    @SuppressWarnings("null")
    @Override
    @Nullable
    public Predicate toPredicate(Root<PermissionEntity> arg0, @Nullable CriteriaQuery<?> arg1, CriteriaBuilder arg2) {
        return null;
    }

    public Specification<PermissionEntity> getSpecificationByFilters(Map<String, String> params) {
        Specification<PermissionEntity> specification = this.filterByRead();
        for (Entry<String, String> entry : params.entrySet()) {
            switch (entry.getKey()) {
                case "profile":
                    specification = specification.and(this.filterByProfileId(entry.getValue()));
                    break;
                case "module":
                    specification = specification.and(this.filterByModuleId(entry.getValue()));
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

    private Specification<PermissionEntity> filterByProfileId(String profileId) {
        return (root, query, cb) -> cb.equal(root.<UUID>get("profile").get("id"), UUID.fromString(profileId));
    }

    private Specification<PermissionEntity> filterByModuleId(String moduleId) {
        return (root, query, cb) -> cb.equal(root.<UUID>get("module").get("id"), UUID.fromString(moduleId));
    }

    private Specification<PermissionEntity> filterByCompanyId(String companyId) {
        return (root, query, cb) -> cb.equal(root.get("company").get("id"), UUID.fromString(companyId));
    }

    private Specification<PermissionEntity> filterByRead() {
        return (root, query, cb) -> cb.equal(root.<Boolean>get("canRead"), true);
    }

}
