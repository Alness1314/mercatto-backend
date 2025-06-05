package com.mercatto.sales.profiles.entity;

import java.util.Set;

import com.mercatto.sales.common.model.entity.CommonEntity;
import com.mercatto.sales.permissions.entity.PermissionEntity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "profiles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class ProfileEntity extends CommonEntity {

    @Column(nullable = false, unique = true, columnDefinition = "character varying(64)")
    private String name;

    @OneToMany(mappedBy = "profile", cascade = CascadeType.ALL)
    private Set<PermissionEntity> permissions;

    @Override
    public String toString() {
        return "ProfileEntity [id=" + getId() + ", name=" + name + ", erased=" + getErased() + "]";
    }

}
