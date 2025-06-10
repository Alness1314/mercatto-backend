package com.mercatto.sales.profiles.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.mercatto.sales.common.keys.Filters;
import com.mercatto.sales.common.model.ResponseServerDto;
import com.mercatto.sales.company.entity.CompanyEntity;
import com.mercatto.sales.company.repository.CompanyRepository;
import com.mercatto.sales.config.GenericMapper;
import com.mercatto.sales.profiles.dto.request.ProfileRequest;
import com.mercatto.sales.profiles.dto.response.ProfileResponse;
import com.mercatto.sales.profiles.entity.ProfileEntity;
import com.mercatto.sales.profiles.repository.ProfileRepository;
import com.mercatto.sales.profiles.service.ProfileService;
import com.mercatto.sales.profiles.specification.ProfileSpecification;

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

    @Override
    public ProfileResponse save(String companyId, ProfileRequest request) {
        CompanyEntity company = companyRepository.findById(UUID.fromString(companyId))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Company not found"));

        ProfileEntity newProfile = mapper.map(request, ProfileEntity.class);
        try {
            newProfile.setCompany(company);
            newProfile = profileRepository.save(newProfile);
            return mapperDto(newProfile);
        } catch (Exception e) {
            log.error("Error to save profile ", e);
            throw new ResponseStatusException(HttpStatus.METHOD_NOT_ALLOWED);
        }
    }

    @Override
    public ProfileResponse findOne(String companyId, String id) {
        Map<String, String> params = Map.of(Filters.KEY_ID, id, Filters.KEY_COMPANY_ID, companyId);
        ProfileEntity profile = profileRepository.findOne(filterWithParameters(params))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, ""));
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
        throw new UnsupportedOperationException("Unimplemented method 'update'");
    }

    @Override
    public ResponseServerDto delete(String companyId, String id) {
        throw new UnsupportedOperationException("Unimplemented method 'delete'");
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
            return new ResponseServerDto("the profiles have been created.", HttpStatus.ACCEPTED, true,
                    Map.of("data", profiles));
        } catch (Exception e) {
            log.error("Error to save all profiles ", e);
            return new ResponseServerDto("An error occurred while creating the profiles", HttpStatus.METHOD_NOT_ALLOWED,
                    false,
                    null);
        }
    }
}
