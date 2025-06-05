package com.mercatto.sales.states.entity;

import java.io.Serializable;

import com.mercatto.sales.common.model.entity.CommonEntity;
import com.mercatto.sales.country.entity.CountryEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@Entity
@Table(name = "states")
@SuperBuilder @AllArgsConstructor @NoArgsConstructor
public class StateEntity extends CommonEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    @Column(nullable = false, columnDefinition = "character varying(64)")
    private String name;

    @ManyToOne
    @JoinColumn(name = "country_id", columnDefinition = "uuid", nullable = false)
    private CountryEntity country;
}
