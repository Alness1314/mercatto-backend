package com.mercatto.sales.profiles.entity;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

import com.mercatto.sales.company.entity.CompanyEntity;
import com.mercatto.sales.permissions.entity.PermissionEntity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "profiles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProfileEntity {
    @Id
    @GeneratedValue(generator = "uuid2")
    @Column(name = "id", updatable = false, nullable = false, columnDefinition = "uuid")
    private UUID id;

    @Column(nullable = false, unique = true, columnDefinition = "character varying(64)")
    private String name;

    @OneToMany(mappedBy = "profile", cascade = CascadeType.ALL)
    private Set<PermissionEntity> permissions;

    @Column(name = "create_at", nullable = false, updatable = false, columnDefinition = "timestamp without time zone")
    private LocalDateTime createAt;

    @Column(name = "update_at", nullable = false, updatable = true, columnDefinition = "timestamp without time zone")
    private LocalDateTime updateAt;

    @ManyToOne
    @JoinColumn(name = "company_id", nullable = true)
    private CompanyEntity company;

    @Column(nullable = false, columnDefinition = "boolean")
    private Boolean erased;

    @PrePersist
    public void prePersist() {
        setErased(false);
        setCreateAt(LocalDateTime.now());
        setUpdateAt(LocalDateTime.now());
    }

    @PreUpdate
    public void preUpdate() {
        setUpdateAt(LocalDateTime.now());
    }

     @Override
    public String toString() {
        return "ProfileEntity [id=" + getId() + ", name=" + name + ", erased=" + getErased() + "]";
    }

}
