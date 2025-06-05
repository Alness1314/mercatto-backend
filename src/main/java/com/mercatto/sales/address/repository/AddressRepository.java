package com.mercatto.sales.address.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.mercatto.sales.address.entity.AddressEntity;

public interface AddressRepository extends JpaRepository<AddressEntity, UUID>, JpaSpecificationExecutor<AddressEntity>{
    
}
