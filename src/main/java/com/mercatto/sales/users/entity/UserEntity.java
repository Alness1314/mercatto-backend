package com.mercatto.sales.users.entity;

import com.mercatto.sales.common.model.entity.CommonEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import com.mercatto.sales.profiles.entity.ProfileEntity;

import java.util.UUID;

@Entity
@Table(name = "users")
@Getter
@Setter
public class UserEntity extends CommonEntity {

    @Column(nullable = false, unique = true, columnDefinition = "character varying(64)")
    private String username;

    @Column(nullable = false, columnDefinition = "character varying(256)")
    private String password;

    @Column(nullable = false, columnDefinition = "character varying(256)")
    private String fullName;

    @Column(name = "send_expiration_alert", nullable = false, columnDefinition = "boolean")
    private Boolean sendExpirationAlert;

    @Column(name = "company_id", nullable = true, columnDefinition = "uuid")
    private UUID companyId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "profile_id", nullable = false)
    private ProfileEntity profile;

}
