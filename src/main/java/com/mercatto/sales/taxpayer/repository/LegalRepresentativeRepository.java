package com.mercatto.sales.taxpayer.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mercatto.sales.taxpayer.entity.LegalRepresentativeEntity;

public interface LegalRepresentativeRepository extends JpaRepository<LegalRepresentativeEntity, UUID>{
    
}
