package com.mercatto.sales.taxpayer.entity;

import com.mercatto.sales.address.entity.AddressEntity;
import com.mercatto.sales.common.model.entity.CommonEntity;

import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;

public class LegalRepresentativeEntity extends CommonEntity{
    @Column(nullable = false, columnDefinition = "character varying(13)")
    private String fullName;

    @Column(nullable = false, columnDefinition = "character varying(13)")
    private String rfc;

    @Column(name = "data_key", nullable = false, columnDefinition = "character varying(64)")
    private String dataKey;

    @OneToOne
    @JoinColumn(name = "taxpayer_id", nullable = false)
    private TaxpayerEntity taxpayer;

    @ManyToOne
    @JoinColumn(name = "address_id", nullable = false)
    private AddressEntity address;
}
