package com.mercatto.sales.settings.specification;

import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.lang.Nullable;

import com.mercatto.sales.settings.entity.SettingsEntity;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

public class SettingsSpecification implements Specification<SettingsEntity> {

    @SuppressWarnings("null")
    @Override
    @Nullable
    public Predicate toPredicate(Root<SettingsEntity> arg0, @Nullable CriteriaQuery<?> arg1, CriteriaBuilder arg2) {
        return null;
    }

    public Specification<SettingsEntity> getSpecificationByFilters(Map<String, String> params) {
        Specification<SettingsEntity> specification = this.filterByErased("false");
        for (Entry<String, String> entry : params.entrySet()) {
            switch (entry.getKey()) {
                case "id":
                    specification = specification.and(this.filterById(entry.getValue()));
                    break;
                case "key":
                    specification = specification.and(this.filterByKey(entry.getValue()));
                    break;
                case "erased":
                    specification = specification.and(this.filterByErased(entry.getValue()));
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

    private Specification<SettingsEntity> filterById(String id) {
        return (root, query, cb) -> cb.equal(root.<UUID>get("id"), UUID.fromString(id));
    }

    private Specification<SettingsEntity> filterByCompanyId(String companyId) {
        return (root, query, cb) -> cb.equal(root.get("company").get("id"), UUID.fromString(companyId));
    }

    private Specification<SettingsEntity> filterByKey(String key) {
        return (root, query, cb) -> cb.equal(root.<String>get("key"), key);

    }

    private Specification<SettingsEntity> filterByErased(String enabled) {
        return (root, query, cb) -> cb.equal(root.<Boolean>get("erased"), Boolean.parseBoolean(enabled));
    }

}
