package com.mercatto.sales.app.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import com.mercatto.sales.app.service.AppConfigService;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

@Configuration
@Slf4j
public class AppConfiguration {
     @Autowired
    private AppConfigService appConfigService;

    @PostConstruct
    public void init() {
        log.info("Response1: {}", appConfigService.createDefaultCountries());
        log.info("Response2: {}", appConfigService.createDefaultStates());
        log.info("Response3: {}", appConfigService.createDefaultCities());
        log.info("Response4: {}", appConfigService.createDefaultProfiles());
        log.info("Response5: {}", appConfigService.createDefaultUser());
    }
}
