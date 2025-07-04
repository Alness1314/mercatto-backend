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
        log.info("Response countries: {}", appConfigService.createDefaultCountries());
        log.info("Response states: {}", appConfigService.createDefaultStates());
        log.info("Response cities: {}", appConfigService.createDefaultCities());
        log.info("Response profiles: {}", appConfigService.createDefaultProfiles());
        log.info("Response user: {}", appConfigService.createDefaultUser());
    }
}
