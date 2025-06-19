package com.mercatto.sales.settings.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.mercatto.sales.common.api.ApiCodes;
import com.mercatto.sales.common.keys.Filters;
import com.mercatto.sales.common.messages.Messages;
import com.mercatto.sales.common.model.ResponseServerDto;
import com.mercatto.sales.company.entity.CompanyEntity;
import com.mercatto.sales.company.repository.CompanyRepository;
import com.mercatto.sales.config.GenericMapper;
import com.mercatto.sales.exceptions.RestExceptionHandler;
import com.mercatto.sales.settings.dto.request.SettingsRequest;
import com.mercatto.sales.settings.dto.response.SettingsResponse;
import com.mercatto.sales.settings.entity.SettingsEntity;
import com.mercatto.sales.settings.repository.SettingsRepository;
import com.mercatto.sales.settings.service.SettingsService;
import com.mercatto.sales.settings.specification.SettingsSpecification;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class SettingsServiceImpl implements SettingsService {
    @Autowired
    private SettingsRepository settingsRepository;

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private GenericMapper mapper;

    ModelMapper mapperUpdate = new ModelMapper();

    @PostConstruct
    private void init() {
        mapperUpdate.getConfiguration()
                .setSkipNullEnabled(true)
                .setFieldMatchingEnabled(true)
                .setMatchingStrategy(MatchingStrategies.STRICT);
    }

    @Override
    public List<SettingsResponse> find(String companyId, Map<String, String> params) {
        Map<String, String> paramsNew = new HashMap<>(params);
        paramsNew.put(Filters.KEY_COMPANY_ID, companyId);
        Specification<SettingsEntity> specification = filterWithParameters(paramsNew);
        return settingsRepository.findAll(specification)
                .stream()
                .map(this::mapperDto)
                .toList();
    }

    @Override
    public SettingsResponse findOne(String companyId, String id) {
        Map<String, String> params = Map.of(Filters.KEY_ID, id, Filters.KEY_COMPANY_ID, companyId);
        SettingsEntity category = settingsRepository.findOne(filterWithParameters(params))
                .orElseThrow(() -> new RestExceptionHandler(ApiCodes.API_CODE_404, HttpStatus.NOT_FOUND,
                        "Category not found"));
        return mapperDto(category);
    }

    @Override
    public SettingsResponse save(String companyId, SettingsRequest request) {
        CompanyEntity company = companyRepository.findById(UUID.fromString(companyId))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Company not found"));
        SettingsEntity setting = mapper.map(request, SettingsEntity.class);
        try {
            setting.setCompany(company);
            setting = settingsRepository.save(setting);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("Error to save settings {}", e.getMessage());
            throw new RestExceptionHandler(ApiCodes.API_CODE_500, HttpStatus.INTERNAL_SERVER_ERROR,
                    "Error to save settings");
        }
        return mapperDto(setting);
    }

    @Override
    public SettingsResponse update(String companyId, String id, SettingsRequest request) {
        // Verificar que la compañía existe
        CompanyEntity company = companyRepository.findById(UUID.fromString(companyId))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Company not found"));

        // Buscar la configuración existente
        Map<String, String> params = Map.of(Filters.KEY_ID, id, Filters.KEY_COMPANY_ID, companyId);
        SettingsEntity existingSetting = settingsRepository.findOne(filterWithParameters(params))
                .orElseThrow(() -> new RestExceptionHandler(ApiCodes.API_CODE_404, HttpStatus.NOT_FOUND,
                        "Settings not found"));

        try {
            // Actualizar los campos de la entidad existente con los valores del request
            mapperUpdate.map(request, existingSetting);
            existingSetting.setCompany(company); // Asegurar que la relación con la compañía se mantiene

            // Guardar los cambios
            SettingsEntity updatedSetting = settingsRepository.save(existingSetting);
            return mapperDto(updatedSetting);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("Error updating settings {}", e.getMessage());
            throw new RestExceptionHandler(ApiCodes.API_CODE_500, HttpStatus.INTERNAL_SERVER_ERROR,
                    "Error updating settings");
        }

    }

    @Override
    public ResponseServerDto delete(String companyId, String id) {
        Map<String, String> params = Map.of(Filters.KEY_ID, id, Filters.KEY_COMPANY_ID, companyId);
        SettingsEntity existingSetting = settingsRepository.findOne(filterWithParameters(params))
                .orElseThrow(() -> new RestExceptionHandler(ApiCodes.API_CODE_404, HttpStatus.NOT_FOUND,
                        "Settings not found"));
        try {
            existingSetting.setErased(true);
            settingsRepository.save(existingSetting);
            return new ResponseServerDto(String.format(Messages.DELETE_ENTITY, id), HttpStatus.ACCEPTED, true);
        } catch (Exception e) {
            throw new RestExceptionHandler(ApiCodes.API_CODE_409, HttpStatus.CONFLICT,
                    String.format(Messages.ERROR_TO_SAVE_ENTITY, e.getMessage()));
        }
    }

    private SettingsResponse mapperDto(SettingsEntity source) {
        return mapper.map(source, SettingsResponse.class);
    }

    public Specification<SettingsEntity> filterWithParameters(Map<String, String> parameters) {
        return new SettingsSpecification().getSpecificationByFilters(parameters);
    }

}
