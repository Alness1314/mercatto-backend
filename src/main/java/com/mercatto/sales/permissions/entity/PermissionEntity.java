package com.mercatto.sales.permissions.entity;

import org.springframework.context.annotation.Profile;

import com.mercatto.sales.modules.entity.ModulesEntity;

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
    @JoinColumn(name = "profileId", insertable = false, updatable = false)
    private Profile profile;

    @ManyToOne
    @JoinColumn(name = "moduleId", insertable = false, updatable = false)
    private ModulesEntity module;

    @Column(nullable = false)
    private boolean canRead;

    @Column(nullable = false)
    private boolean canWrite;

    @Column(nullable = false)
    private boolean canDelete;
}
