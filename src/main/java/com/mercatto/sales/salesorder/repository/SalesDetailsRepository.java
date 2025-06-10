package com.mercatto.sales.salesorder.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.mercatto.sales.salesorder.entity.SalesDetailsEntity;

public interface SalesDetailsRepository extends JpaRepository<SalesDetailsEntity, UUID>, JpaSpecificationExecutor<SalesDetailsEntity>{
    
}
