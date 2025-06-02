package com.mercatto.sales.profiles.entity;

import java.io.Serializable;

import com.mercatto.sales.common.model.entity.CommonEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "profiles")
@Getter
@Setter
@Builder
public class ProfileEntity extends CommonEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    @Column(nullable = false, unique = true, columnDefinition = "character varying(64)")
    private String name;

    @Override
    public String toString() {
        return "ProfileEntity [id=" + getId() + ", name=" + name + ", erased=" + getErased() + "]";
    }

}
