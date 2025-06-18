package com.mercatto.sales.modules.specification;

import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.lang.Nullable;

import com.mercatto.sales.modules.entity.ModulesEntity;
import com.mercatto.sales.permissions.entity.PermissionEntity;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

public class ModuleSpecification implements Specification<ModulesEntity> {
    private String permissions = "permissions";

    @SuppressWarnings("null")
    @Override
    @Nullable
    public Predicate toPredicate(Root<ModulesEntity> arg0, @Nullable CriteriaQuery<?> arg1, CriteriaBuilder arg2) {
        return null;
    }

    public Specification<ModulesEntity> getSpecificationByFilters(Map<String, String> params) {
        Specification<ModulesEntity> specification = this.filterByErased("false");
        for (Entry<String, String> entry : params.entrySet()) {
            switch (entry.getKey()) {
                case "id":
                    specification = specification.and(this.filterById(entry.getValue()));
                    break;
                case "name":
                    specification = specification.and(filterByName(entry.getValue()));
                    break;
                case "profileId":
                    specification = specification.and(filterByProfileId(entry.getValue()));
                    break;
                case "canRead":
                    specification = specification.and(filterByPermission("canRead", entry.getValue()));
                    break;
                case "canWrite":
                    specification = specification.and(filterByPermission("canWrite", entry.getValue()));
                    break;
                case "canDelete":
                    specification = specification.and(filterByPermission("canDelete", entry.getValue()));
                    break;
                case "erased":
                    specification = specification.and(this.filterByErased(entry.getValue()));
                    break;
                default:
                    break;
            }
        }
        return specification;
    }

    private Specification<ModulesEntity> filterById(String id) {
        return (root, query, cb) -> cb.equal(root.<UUID>get("id"), UUID.fromString(id));
    }

    private Specification<ModulesEntity> filterByErased(String enabled) {
        return (root, query, cb) -> cb.equal(root.<Boolean>get("erased"), Boolean.parseBoolean(enabled));
    }

    public static Specification<ModulesEntity> filterByName(String name) {
        return (root, query, cb) -> cb.equal(cb.lower(root.get("name")), name.toLowerCase());
    }

    private Specification<ModulesEntity> filterByProfileId(String profileId) {
        return (root, query, cb) -> {
            Join<ModulesEntity, PermissionEntity> join = root.join(permissions, JoinType.INNER);
            return cb.equal(join.get("profile").get("id"), UUID.fromString(profileId));
        };
    }

    private Specification<ModulesEntity> filterByPermission(String permissionField, String value) {
        return (root, query, cb) -> {
            Join<ModulesEntity, PermissionEntity> join = root.join(permissions, JoinType.INNER);
            return cb.equal(join.get(permissionField), Boolean.parseBoolean(value));
        };
    }

    @SuppressWarnings("null")
    public static Specification<ModulesEntity> byParentIdAndProfile(UUID parentId, UUID profileId) {
        return (root, query, cb) -> {
            Join<ModulesEntity, PermissionEntity> join = root.join("permissions", JoinType.INNER);
            Predicate byParent = cb.equal(root.get("parent").get("id"), parentId);
            Predicate byProfile = cb.equal(join.get("profile").get("id"), profileId);
            query.distinct(true);
            return cb.and(byParent, byProfile);
        };
    }

}
