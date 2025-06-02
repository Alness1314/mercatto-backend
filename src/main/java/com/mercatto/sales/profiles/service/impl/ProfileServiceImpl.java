package com.mercatto.sales.profiles.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.mercatto.sales.common.keys.Filters;
import com.mercatto.sales.common.model.ResponseServerDto;
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
    private GenericMapper mapper;

    @Override
    public ProfileResponse save(ProfileRequest request) {
        ProfileEntity newProfile = mapper.map(request, ProfileEntity.class);
        try {
            newProfile = profileRepository.save(newProfile);
            return mapperDto(newProfile);
        } catch (Exception e) {
            log.error("Error to save profile ", e);
            throw new ResponseStatusException(HttpStatus.METHOD_NOT_ALLOWED);
        }
    }

    @Override
    public ProfileResponse findOne(String id) {
        ProfileEntity profile = profileRepository.findOne(filterWithParameters(Map.of(Filters.KEY_ID, id)))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        return mapperDto(profile);
    }

    @Override
    public List<ProfileResponse> find(Map<String, String> params) {
        return profileRepository.findAll(filterWithParameters(params))
                .stream().map(this::mapperDto).toList();
    }

    @Override
    public ProfileResponse update(String id, ProfileRequest request) {
        throw new UnsupportedOperationException("Unimplemented method 'update'");
    }

    @Override
    public ResponseServerDto delete(String id) {
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
    public ResponseServerDto multiSaving(List<ProfileRequest> profileList) {
        try {
            List<ProfileEntity> profiles = profileList.stream()
                    .map(req -> ProfileEntity.builder()
                            .name(req.getName())
                            .build())
                    .toList();
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
