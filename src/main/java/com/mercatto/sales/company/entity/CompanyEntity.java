package com.mercatto.sales.company.entity;

import java.time.LocalDateTime;

import com.mercatto.sales.address.entity.AddressEntity;
import com.mercatto.sales.common.model.entity.CommonEntity;
import com.mercatto.sales.files.entity.FileEntity;
import com.mercatto.sales.taxpayer.entity.TaxpayerEntity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;

public class CompanyEntity extends CommonEntity{
    @Column(name = "name", nullable = false, columnDefinition = "character varying(128)")
    private String name;

    @Column(name = "description", nullable = false, columnDefinition = "character varying(256)")
    private String description;

    @Column(name = "email", nullable = false, columnDefinition = "character varying(32)")
    private String email;

    @Column(name = "phone", nullable = false, columnDefinition = "character varying(20)")
    private String phone;

    @ManyToOne
    @JoinColumn(name = "address_id", nullable = false)
    private AddressEntity address;

    @OneToOne
    @JoinColumn(name = "image_id", unique = true, nullable = true)
    private FileEntity image;

    @OneToOne(mappedBy = "company", cascade = CascadeType.ALL, orphanRemoval = true)
    private TaxpayerEntity taxpayer;

}
