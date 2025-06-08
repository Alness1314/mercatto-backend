package com.mercatto.sales.taxpayer.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.mercatto.sales.taxpayer.entity.TaxpayerEntity;

public interface TaxpayerRepository extends JpaRepository<TaxpayerEntity, UUID>, JpaSpecificationExecutor<TaxpayerEntity>{
    
}
