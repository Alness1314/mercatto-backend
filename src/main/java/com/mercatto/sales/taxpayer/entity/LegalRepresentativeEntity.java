package com.mercatto.sales.taxpayer.entity;

import java.util.UUID;

import com.mercatto.sales.utils.TextEncrypterUtil;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PostLoad;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "legal_representative")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LegalRepresentativeEntity {
    @Id
    @GeneratedValue(generator = "uuid2")
    @Column(name = "id", updatable = false, nullable = false, columnDefinition = "uuid")
    private UUID id;

    @Column(nullable = false, columnDefinition = "character varying(512)")
    private String fullName;

    @Column(nullable = false, columnDefinition = "character varying(256)")
    private String rfc;

    @Column(name = "data_key", nullable = false, columnDefinition = "character varying(64)")
    private String dataKey;

    @Column(nullable = false, columnDefinition = "boolean")
    private Boolean erased;

    @OneToOne
    @JoinColumn(name = "taxpayer_id", nullable = false)
    private TaxpayerEntity taxpayer;

    @PrePersist
    public void prePersist() {
        setErased(false);

        if (this.dataKey != null) {
            this.rfc = TextEncrypterUtil.encrypt(rfc, dataKey);
            this.fullName = TextEncrypterUtil.encrypt(fullName, dataKey);
        }
    }
}
