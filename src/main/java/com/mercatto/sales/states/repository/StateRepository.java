package com.mercatto.sales.states.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.mercatto.sales.states.entity.StateEntity;

public interface StateRepository extends JpaRepository<StateEntity, UUID>, JpaSpecificationExecutor<StateEntity> {

}
