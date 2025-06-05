package com.mercatto.sales.cities.entity;

import java.io.Serializable;

import com.mercatto.sales.common.model.entity.CommonEntity;
import com.mercatto.sales.states.entity.StateEntity;

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

@Table(name = "cities")
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class CityEntity extends CommonEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    @Column(nullable = false, columnDefinition = "character varying(64)")
    private String name;

    @ManyToOne
    @JoinColumn(name = "state_id", columnDefinition = "uuid", nullable = false)
    private StateEntity state;

}
