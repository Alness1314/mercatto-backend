package com.mercatto.sales.address.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import com.mercatto.sales.cities.entity.CityEntity;
import com.mercatto.sales.country.entity.CountryEntity;
import com.mercatto.sales.states.entity.StateEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "address")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddressEntity{
    @Id
    @GeneratedValue(generator = "uuid2")
    @Column(name = "id", updatable = false, nullable = false, columnDefinition = "uuid")
    private UUID id;

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

    @Column(name = "create_at", nullable = false, updatable = false, columnDefinition = "timestamp without time zone")
    private LocalDateTime createAt;

    @Column(name = "update_at", nullable = false, updatable = true, columnDefinition = "timestamp without time zone")
    private LocalDateTime updateAt;

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

}
