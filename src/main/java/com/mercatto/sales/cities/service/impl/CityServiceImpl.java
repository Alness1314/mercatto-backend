package com.mercatto.sales.cities.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
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
import com.mercatto.sales.common.messages.Messages;
import com.mercatto.sales.common.model.ResponseServerDto;
import com.mercatto.sales.config.GenericMapper;
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
                        String.format(Messages.NOT_FOUND, id)));
        return mapperDto(city);
    }

    @Override
    @Transactional
    public CityResponse save(CityRequest request) {
        StateEntity state = stateRepository.findById(UUID.fromString(request.getStateId()))
                .orElseThrow(() -> new RestExceptionHandler(ApiCodes.API_CODE_404, HttpStatus.NOT_FOUND,
                        String.format(Messages.NOT_FOUND, request.getStateId())));

        CityEntity newCity = mapper.map(request, CityEntity.class);
        try {
            newCity.setState(state);
            newCity = cityRepository.save(newCity);
        } catch (DataIntegrityViolationException ex) {
            log.error(Messages.LOG_ERROR_DATA_INTEGRITY, ex.getMessage(), ex);
            throw new RestExceptionHandler(ApiCodes.API_CODE_400, HttpStatus.BAD_REQUEST,
                    Messages.DATA_INTEGRITY);
        } catch (Exception e) {
            log.error(Messages.LOG_ERROR_TO_SAVE_ENTITY, e.getMessage(), e);
            throw new RestExceptionHandler(ApiCodes.API_CODE_500, HttpStatus.INTERNAL_SERVER_ERROR,
                    Messages.ERROR_ENTITY_SAVE);
        }
        return mapperDto(newCity);
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
                        String.format(Messages.NOT_FOUND, stateId)));

        List<CityEntity> cities = new ArrayList<>();
        citiesList.stream().forEach(item -> cities.add(CityEntity.builder()
                .name(item)
                .state(state)
                .build()));

        try {
            cityRepository.saveAll(cities);
            return new ResponseServerDto(Messages.CITY_CREATE, HttpStatus.ACCEPTED, true, null);
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
