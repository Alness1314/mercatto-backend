package com.mercatto.sales.profiles.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
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
import com.mercatto.sales.profiles.dto.request.ProfileRequest;
import com.mercatto.sales.profiles.dto.response.ProfileResponse;
import com.mercatto.sales.profiles.entity.ProfileEntity;
import com.mercatto.sales.profiles.repository.ProfileRepository;
import com.mercatto.sales.profiles.service.ProfileService;
import com.mercatto.sales.profiles.specification.ProfileSpecification;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ProfileServiceImpl implements ProfileService {
    @Autowired
    private ProfileRepository profileRepository;

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
    public ProfileResponse save(String companyId, ProfileRequest request) {
        CompanyEntity company = companyRepository.findById(UUID.fromString(companyId))
                .orElseThrow(() -> new RestExceptionHandler(ApiCodes.API_CODE_404, HttpStatus.NOT_FOUND,
                        String.format(Messages.NOT_FOUND, companyId)));

        ProfileEntity newProfile = mapper.map(request, ProfileEntity.class);
        try {
            newProfile.setCompany(company);
            newProfile = profileRepository.save(newProfile);
            return mapperDto(newProfile);
        } catch (DataIntegrityViolationException ex) {
            log.error(Messages.LOG_ERROR_DATA_INTEGRITY, ex.getMessage(), ex);
            throw new RestExceptionHandler(ApiCodes.API_CODE_400, HttpStatus.BAD_REQUEST,
                    Messages.DATA_INTEGRITY);
        } catch (Exception e) {
            log.error(Messages.LOG_ERROR_TO_SAVE_ENTITY, e.getMessage(), e);
            throw new RestExceptionHandler(ApiCodes.API_CODE_500, HttpStatus.INTERNAL_SERVER_ERROR,
                    Messages.ERROR_ENTITY_SAVE);
        }
    }

    @Override
    public ProfileResponse findOne(String companyId, String id) {
        Map<String, String> params = Map.of(Filters.KEY_ID, id, Filters.KEY_COMPANY_ID, companyId);
        ProfileEntity profile = profileRepository.findOne(filterWithParameters(params))
                .orElseThrow(() -> new RestExceptionHandler(ApiCodes.API_CODE_404, HttpStatus.NOT_FOUND,
                        String.format(Messages.NOT_FOUND, id)));
        return mapperDto(profile);
    }

    @Override
    public List<ProfileResponse> find(String companyId, Map<String, String> params) {
        Map<String, String> paramsNew = new HashMap<>(params);
        paramsNew.put(Filters.KEY_COMPANY_ID, companyId);
        return profileRepository.findAll(filterWithParameters(paramsNew))
                .stream().map(this::mapperDto).toList();
    }

    @Override
    public ProfileResponse update(String companyId, String id, ProfileRequest request) {
        // Verificar que la compañía existe
        companyRepository.findById(UUID.fromString(companyId))
                .orElseThrow(() -> new RestExceptionHandler(ApiCodes.API_CODE_404, HttpStatus.NOT_FOUND,
                        String.format(Messages.NOT_FOUND, companyId)));

        // Buscar el perfil existente
        Map<String, String> params = Map.of(Filters.KEY_ID, id, Filters.KEY_COMPANY_ID, companyId);
        ProfileEntity existingProfile = profileRepository.findOne(filterWithParameters(params))
                .orElseThrow(
                        () -> new ResponseStatusException(HttpStatus.NOT_FOUND, String.format(Messages.NOT_FOUND, id)));

        try {
            // Actualizar los campos del perfil con los nuevos valores
            mapperUpdate.map(request, existingProfile);

            // Guardar los cambios
            ProfileEntity updatedProfile = profileRepository.save(existingProfile);
            return mapperDto(updatedProfile);
        } catch (DataIntegrityViolationException ex) {
            log.error(Messages.LOG_ERROR_DATA_INTEGRITY, ex.getMessage(), ex);
            throw new RestExceptionHandler(ApiCodes.API_CODE_400, HttpStatus.BAD_REQUEST,
                    Messages.DATA_INTEGRITY);
        } catch (Exception e) {
            log.error(Messages.LOG_ERROR_TO_UPDATE_ENTITY, e.getMessage(), e);
            throw new RestExceptionHandler(ApiCodes.API_CODE_500, HttpStatus.INTERNAL_SERVER_ERROR,
                    Messages.ERROR_ENTITY_UPDATE);
        }
    }

    @Override
    public ResponseServerDto delete(String companyId, String id) {
        // Buscar el perfil existente
        Map<String, String> params = Map.of(Filters.KEY_ID, id, Filters.KEY_COMPANY_ID, companyId);
        ProfileEntity existingProfile = profileRepository.findOne(filterWithParameters(params))
                .orElseThrow(() -> new RestExceptionHandler(ApiCodes.API_CODE_404, HttpStatus.NOT_FOUND,
                        String.format(Messages.NOT_FOUND, id)));
        try {
            existingProfile.setErased(true);
            profileRepository.save(existingProfile);
            return new ResponseServerDto(String.format(Messages.ENTITY_DELETE, id), HttpStatus.ACCEPTED, true);
        } catch (DataIntegrityViolationException ex) {
            log.error(Messages.LOG_ERROR_DATA_INTEGRITY, ex.getMessage(), ex);
            throw new RestExceptionHandler(ApiCodes.API_CODE_400, HttpStatus.BAD_REQUEST,
                    Messages.DATA_INTEGRITY);
        } catch (Exception e) {
            log.error(Messages.LOG_ERROR_TO_DELETE_ENTITY, e.getMessage(), e);
            throw new RestExceptionHandler(ApiCodes.API_CODE_409, HttpStatus.CONFLICT,
                    String.format(Messages.ERROR_ENTITY_DELETE, e.getMessage()));
        }
    }

    private ProfileResponse mapperDto(ProfileEntity source) {
        return mapper.map(source, ProfileResponse.class);
    }

    public Specification<ProfileEntity> filterWithParameters(Map<String, String> parameters) {
        return new ProfileSpecification().getSpecificationByFilters(parameters);
    }

    @Override
    public ProfileResponse findByName(String name) {
        ProfileEntity profile = profileRepository.findOne(filterWithParameters(Map.of(Filters.KEY_NAME, name)))
                .orElse(null);
        if (profile == null) {
            return null;
        }
        return mapperDto(profile);
    }

    @Override
    public ResponseServerDto multiSaving(String companyId, List<ProfileRequest> profileList) {
        try {
            List<ProfileEntity> profiles = new ArrayList<>();
            profileList.stream().forEach(item -> profiles.add(ProfileEntity.builder()
                    .name(item.getName())
                    .build()));

            profileRepository.saveAll(profiles);
            return new ResponseServerDto(Messages.PROFILES_CREATE, HttpStatus.ACCEPTED, true,
                    Map.of("data", profiles));
        } catch (DataIntegrityViolationException ex) {
            log.error(Messages.LOG_ERROR_DATA_INTEGRITY, ex.getMessage(), ex);
            throw new RestExceptionHandler(ApiCodes.API_CODE_400, HttpStatus.BAD_REQUEST,
                    Messages.DATA_INTEGRITY);
        } catch (Exception e) {
            log.error(Messages.LOG_ERROR_TO_SAVE_ENTITY, e.getMessage(), e);
            throw new RestExceptionHandler(ApiCodes.API_CODE_500, HttpStatus.INTERNAL_SERVER_ERROR,
                    Messages.ERROR_ENTITY_SAVE);
        }
    }
}
