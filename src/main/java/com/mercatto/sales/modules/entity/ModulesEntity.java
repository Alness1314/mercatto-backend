package com.mercatto.sales.modules.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

import com.mercatto.sales.common.model.entity.CommonEntity;
import com.mercatto.sales.permissions.entity.PermissionEntity;

@Entity
@Table(name = "modules")
@Getter
@Setter
public class ModulesEntity extends CommonEntity {
    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String route;

    @Column(name = "iconName", nullable = false)
    private String iconName;

    @Column(nullable = false)
    private boolean deleted;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parentModuleId")
    private ModulesEntity parent;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL)
    private Set<ModulesEntity> submodules;

    @OneToMany(mappedBy = "module", cascade = CascadeType.ALL)
    private Set<PermissionEntity> permissions;
}
