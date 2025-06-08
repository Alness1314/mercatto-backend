package com.mercatto.sales.transactions.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.mercatto.sales.transactions.entity.SalesEntity;

public interface SalesRepository extends JpaRepository<SalesEntity, UUID>, JpaSpecificationExecutor<SalesEntity> {
}
