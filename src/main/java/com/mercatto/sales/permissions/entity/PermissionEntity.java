package com.mercatto.sales.permissions.entity;

import com.mercatto.sales.modules.entity.ModulesEntity;
import com.mercatto.sales.profiles.entity.ProfileEntity;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "permissions")
@Getter
@Setter
public class PermissionEntity {
    @EmbeddedId
    private PermissionId id;

    @ManyToOne
    @JoinColumn(name = "profile_id", insertable = false, updatable = false)
    private ProfileEntity profile;

    @ManyToOne
    @JoinColumn(name = "module_id", insertable = false, updatable = false)
    private ModulesEntity module;

    @Column(name = "can_create", nullable = false, columnDefinition = "boolean")
    private boolean canCreate;

    @Column(name = "can_read", nullable = false, columnDefinition = "boolean")
    private boolean canRead;

    @Column(name = "can_update", nullable = false, columnDefinition = "boolean")
    private boolean canUpdate;

    @Column(name = "can_delete", nullable = false, columnDefinition = "boolean")
    private boolean canDelete;
}
