package com.mercatto.sales.unit.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.mercatto.sales.unit.entity.UnitMeasurement;

public interface UnitMeasurementRepo extends JpaRepository<UnitMeasurement, UUID>, JpaSpecificationExecutor<UnitMeasurement> {

    // Custom query methods can be defined here if needed
    
}
