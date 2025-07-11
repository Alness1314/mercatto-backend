package com.mercatto.sales.taxpayer.entity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import com.mercatto.sales.address.entity.AddressEntity;
import com.mercatto.sales.company.entity.CompanyEntity;
import com.mercatto.sales.utils.TextEncrypterUtil;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Entity
@Table(name = "taxpayer")
@Getter
@Setter
@Slf4j
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaxpayerEntity {
    @Id
    @GeneratedValue(generator = "uuid2")
    @Column(name = "id", updatable = false, nullable = false, columnDefinition = "uuid")
    private UUID id;

    @Column(name = "corporate_reason_or_natural_person", nullable = false, columnDefinition = "character varying(512)")
    private String corporateReasonOrNaturalPerson;

    @Column(nullable = false, columnDefinition = "character varying(256)")
    private String rfc;

    @Column(name = "type_person", nullable = false, columnDefinition = "character varying(64)")
    private String typePerson;

    @OneToOne(mappedBy = "taxpayer", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private LegalRepresentativeEntity legalRepresentative;

    @ManyToOne
    @JoinColumn(name = "address_id", nullable = false)
    private AddressEntity address;

    @Column(name = "data_key", nullable = false, columnDefinition = "character varying(64)")
    private String dataKey;

    @OneToMany(mappedBy = "taxpayer", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<CompanyEntity> companies;

    @Column(name = "create_at", nullable = false, updatable = false, columnDefinition = "timestamp without time zone")
    private LocalDateTime createAt;

    @Column(name = "update_at", nullable = false, updatable = true, columnDefinition = "timestamp without time zone")
    private LocalDateTime updateAt;

    @Column(nullable = false, columnDefinition = "boolean")
    private Boolean erased;

    @Override
    public String toString() {
        return "TaxpayerEntity [id=" + id + ", corporateReasonOrNaturalPerson=" + corporateReasonOrNaturalPerson
                + ", rfc=" + rfc + ", typePerson=" + typePerson + ", legalRepresentative=" + legalRepresentative
                + ", address=" + address + ", dataKey=" + dataKey + ", createAt=" + createAt + ", updateAt=" + updateAt
                + ", erased=" + erased + "]";
    }

    @PrePersist
    public void prePersist() {
        setErased(false);
        setCreateAt(LocalDateTime.now());
        setUpdateAt(LocalDateTime.now());
        encryptData(this);
    }

    @PreUpdate
    public void preUpdate() {
        setUpdateAt(LocalDateTime.now());
        encryptData(this);
    }

    private void encryptData(TaxpayerEntity source) {
        if (source.getDataKey() != null) {
            String encCorporate = TextEncrypterUtil.encrypt(source.getCorporateReasonOrNaturalPerson(),
                    source.getDataKey());
            String rfcCorporate = TextEncrypterUtil.encrypt(source.getRfc(), source.getDataKey());
            source.setCorporateReasonOrNaturalPerson(encCorporate);
            source.setRfc(rfcCorporate);
        }
    }

}
