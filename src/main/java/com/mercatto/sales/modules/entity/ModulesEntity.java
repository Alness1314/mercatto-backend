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

import java.util.List;
import java.util.Set;

import com.mercatto.sales.common.model.entity.CommonEntity;
import com.mercatto.sales.permissions.entity.PermissionEntity;

@Entity
@Table(name = "modules")
@Getter
@Setter
public class ModulesEntity extends CommonEntity {
    @Column(nullable = false, columnDefinition = "character varying(64)")
    private String name;

    @Column(nullable = false, columnDefinition = "character varying(256)")
    private String route;

    @Column(name = "icon_name", nullable = false, columnDefinition = "character varying(128)")
    private String iconName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_module_id", columnDefinition = "uuid")
    private ModulesEntity parent;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL)
    private List<ModulesEntity> submodules;

    @OneToMany(mappedBy = "module", cascade = CascadeType.ALL)
    private List<PermissionEntity> permissions;
}
