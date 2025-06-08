package com.mercatto.sales.transactions.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import com.mercatto.sales.company.entity.CompanyEntity;
import com.mercatto.sales.users.entity.UserEntity;

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

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "sales")
public class SalesEntity {
    @Id
    @GeneratedValue(generator = "uuid2")
    @Column(name = "id", updatable = false, nullable = false, columnDefinition = "uuid")
    private UUID id;

    @Column(name = "transaction_date_time", nullable = false, columnDefinition = "timestamp without time zone")
    private LocalDateTime transactionDateTime;

    @Column(nullable = false, columnDefinition = "numeric(18,4)")
    private BigDecimal amount;

    @Column(name = "payment_method", nullable = false, columnDefinition = "character varying(64)")
    private String paymentMethod;

    @Column(nullable = false, columnDefinition = "boolean")
    private Boolean sync;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = true)
    private UserEntity user;

    @ManyToOne
    @JoinColumn(name = "company_id", nullable = true)
    private CompanyEntity company;

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
