package com.mercatto.sales.taxpayer.entity;

import com.mercatto.sales.address.entity.AddressEntity;
import com.mercatto.sales.common.model.entity.CommonEntity;
import com.mercatto.sales.company.entity.CompanyEntity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
//import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;

public class TaxpayerEntity extends CommonEntity{
     @Column(name = "corporate_reason_or_natural_person", nullable = false, columnDefinition = "character varying(64)")
    private String corporateReasonOrNaturalPerson;

    @Column(nullable = false, columnDefinition = "character varying(13)")
    private String rfc;

    @Column(name = "type_person", nullable = false, columnDefinition = "character varying(12)")
    private String typePerson;
    
    @OneToOne(mappedBy = "taxpayer", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private LegalRepresentativeEntity legalRepresentative;

    @ManyToOne
    @JoinColumn(name = "address_id", nullable = false)
    private AddressEntity address;

    @Column(name = "data_key", nullable = false, columnDefinition = "character varying(64)")
    private String dataKey;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "company_id", nullable = false)
    private CompanyEntity company;

    //@OneToMany(mappedBy = "taxpayer", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    //private List<SubsidiaryEntity> subsidiaries;
}
