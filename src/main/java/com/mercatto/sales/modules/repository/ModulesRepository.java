package com.mercatto.sales.modules.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.mercatto.sales.modules.entity.ModulesEntity;

public interface ModulesRepository extends JpaRepository<ModulesEntity, UUID>, JpaSpecificationExecutor<ModulesEntity> {

}
