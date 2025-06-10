package com.mercatto.sales.salesorder.entity;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.UUID;

import com.mercatto.sales.products.entity.ProductEntity;
import com.mercatto.sales.transactions.entity.SalesEntity;

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
@Table(name = "sales_details")
public class SalesDetailsEntity {
    @Id
    @GeneratedValue(generator = "uuid2")
    @Column(name = "id", updatable = false, nullable = false, columnDefinition = "uuid")
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "sales_id", nullable = true)
    private SalesEntity sales;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = true)
    private ProductEntity product;

    @Column(nullable = false, columnDefinition = "bigint")
    private BigInteger stock;

    @Column(name = "unit_price", nullable = false, columnDefinition = "numeric(18,4)")
    private BigDecimal unitPrice;

    @Column(nullable = false, columnDefinition = "numeric(18,4)")
    private BigDecimal subtotal;

    @Column(nullable = false, columnDefinition = "numeric(18,4)")
    private BigDecimal tax;

    @Column(name = "company_id", nullable = false, columnDefinition = "uuid")
    private UUID company;

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
