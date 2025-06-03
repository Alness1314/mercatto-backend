package com.mercatto.sales.permissions.entity;

import java.io.Serializable;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Data;

@Data
@Embeddable
public class PermissionId implements Serializable {
    private static final long serialVersionUID = 1L;

    @Column(name = "profileId", nullable = false, columnDefinition = "uuid")
    private UUID profileId;

    @Column(name = "moduleId", nullable = false, columnDefinition = "uuid")
    private UUID moduleId;
}
