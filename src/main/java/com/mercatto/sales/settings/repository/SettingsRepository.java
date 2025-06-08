package com.mercatto.sales.settings.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.mercatto.sales.settings.entity.SettingsEntity;

public interface SettingsRepository extends JpaRepository<SettingsEntity, UUID>, JpaSpecificationExecutor<SettingsEntity> {
}
