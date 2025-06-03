package com.mercatto.sales.country.entity;

import com.mercatto.sales.common.model.entity.CommonEntity;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "country")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CountryEntity extends CommonEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    @Column(nullable = false, columnDefinition = "character varying(64)")
    private String name;

    @Column(name = "code", nullable = true, columnDefinition = "character varying(5)")
    private String code;
}
