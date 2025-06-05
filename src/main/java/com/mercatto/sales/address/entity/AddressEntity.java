package com.mercatto.sales.address.entity;

import com.mercatto.sales.cities.entity.CityEntity;
import com.mercatto.sales.common.model.entity.CommonEntity;
import com.mercatto.sales.country.entity.CountryEntity;
import com.mercatto.sales.states.entity.StateEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "address")
@Getter
@Setter
public class AddressEntity extends CommonEntity{
    @Column(nullable = true, columnDefinition = "character varying(32)")
    private String nickname;

    @Column(nullable = false, columnDefinition = "character varying(64)")
    private String street;

    @Column(nullable = false, columnDefinition = "character varying(15)")
    private String number;

    @Column(nullable = false, columnDefinition = "character varying(32)")
    private String suburb;

    @Column(name = "zip_code", nullable = false, columnDefinition = "character varying(64)")
    private String zipCode;

    @Column(nullable = true, columnDefinition = "character varying(128)")
    private String reference;

    @ManyToOne
    @JoinColumn(name = "country_id", columnDefinition = "uuid", nullable = false)
    private CountryEntity country;

    @ManyToOne
    @JoinColumn(name = "state_id", columnDefinition = "uuid", nullable = false)
    private StateEntity state;

    @ManyToOne
    @JoinColumn(name = "city_id", columnDefinition = "uuid", nullable = false)
    private CityEntity city;

}
