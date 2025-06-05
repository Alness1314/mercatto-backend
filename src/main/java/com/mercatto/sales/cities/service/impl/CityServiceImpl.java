package com.mercatto.sales.cities.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.mercatto.sales.cities.dto.request.CityRequest;
import com.mercatto.sales.cities.dto.response.CityResponse;
import com.mercatto.sales.cities.entity.CityEntity;
import com.mercatto.sales.cities.repository.CityRepository;
import com.mercatto.sales.cities.service.CityService;
import com.mercatto.sales.cities.specification.CitySpecification;
import com.mercatto.sales.common.api.ApiCodes;
import com.mercatto.sales.common.model.ResponseServerDto;
import com.mercatto.sales.config.GenericMapper;
import com.mercatto.sales.country.entity.CountryEntity;
import com.mercatto.sales.exceptions.RestExceptionHandler;
import com.mercatto.sales.states.entity.StateEntity;
import com.mercatto.sales.states.repository.StateRepository;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class CityServiceImpl implements CityService {
    @Autowired
    private CityRepository cityRepository;

    @Autowired
    private StateRepository stateRepository;

    @Autowired
    private GenericMapper mapper;

    @Override
    public List<CityResponse> find(Map<String, String> parameters) {
        return cityRepository.findAll(filterWithParameters(parameters))
                .stream()
                .map(this::mapperDto)
                .toList();
    }

    @Override
    public CityResponse findOne(String id) {
        CityEntity city = cityRepository.findById(UUID.fromString(id))
                .orElseThrow(() -> new RestExceptionHandler(ApiCodes.API_CODE_404, HttpStatus.NOT_FOUND,
                        "City not found."));
        return mapperDto(city);
    }

    @Override
    @Transactional
    public CityResponse save(CityRequest request) {
        StateEntity state = stateRepository.findById(UUID.fromString(request.getStateId()))
                .orElseThrow(() -> new RestExceptionHandler(ApiCodes.API_CODE_404, HttpStatus.NOT_FOUND,
                        "State not found"));

        CityEntity newCity = mapper.map(request, CityEntity.class);
        try {
            newCity.setState(state);
            newCity = cityRepository.save(newCity);
        } catch (Exception e) {
            log.error("Error to save city {}", e.getMessage());
            throw new RestExceptionHandler(ApiCodes.API_CODE_500, HttpStatus.INTERNAL_SERVER_ERROR,
                    "Error to save city");
        }
        return mapperDto(newCity);
    }

    @Override
    public CityResponse update(String id, CityRequest request) {
        throw new UnsupportedOperationException("Unimplemented method 'update'");
    }

    @Override
    public ResponseServerDto delete(String id) {
        CityEntity city = cityRepository.findById(UUID.fromString(id))
                .orElseThrow(() -> new RestExceptionHandler(ApiCodes.API_CODE_404, HttpStatus.NOT_FOUND,
                        "City not found."));
        try {
            city.setErased(true);
            cityRepository.save(city);
            return new ResponseServerDto("The city has been removed.", HttpStatus.ACCEPTED, true, null);
        } catch (Exception e) {
            log.error("Error to delete country ", e);
            return new ResponseServerDto("An error occurred while deleting the city", HttpStatus.METHOD_NOT_ALLOWED,
                    false,
                    null);
        }
    }

    private CityResponse mapperDto(CityEntity source) {
        return mapper.map(source, CityResponse.class);
    }

    private Specification<CityEntity> filterWithParameters(Map<String, String> parameters) {
        return new CitySpecification().getSpecificationByFilters(parameters);
    }

    @Override
    public ResponseServerDto multiSaving(String stateId, List<String> citiesList) {
        StateEntity state = stateRepository.findById(UUID.fromString(stateId))
                .orElseThrow(() -> new RestExceptionHandler(ApiCodes.API_CODE_404, HttpStatus.NOT_FOUND,
                        "State not found"));

        List<CityEntity> cities = new ArrayList<>();
        citiesList.stream().forEach(item -> cities.add(CityEntity.builder()
                .name(item)
                .state(state)
                .build()));

        try {
            cityRepository.saveAll(cities);
            return new ResponseServerDto("the cities have been created.", HttpStatus.ACCEPTED, true, null);
        } catch (Exception e) {
            log.error("Error to save all cities ", e);
            return new ResponseServerDto("An error occurred while creating the cities", HttpStatus.METHOD_NOT_ALLOWED,
                    false,
                    null);
        }
    }
}
