package com.mercatto.sales.states.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.mercatto.sales.common.api.ApiCodes;
import com.mercatto.sales.common.model.ResponseServerDto;
import com.mercatto.sales.config.GenericMapper;
import com.mercatto.sales.country.entity.CountryEntity;
import com.mercatto.sales.country.repository.CountryRepository;
import com.mercatto.sales.exceptions.RestExceptionHandler;
import com.mercatto.sales.profiles.entity.ProfileEntity;
import com.mercatto.sales.states.dto.request.StateRequest;
import com.mercatto.sales.states.dto.response.StateResponse;
import com.mercatto.sales.states.entity.StateEntity;
import com.mercatto.sales.states.repository.StateRepository;
import com.mercatto.sales.states.service.StateService;
import com.mercatto.sales.states.specification.StateSpecification;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class StateServiceImpl implements StateService {
    @Autowired
    private StateRepository stateRepository;

    @Autowired
    private CountryRepository countryRepository;

    @Autowired
    private GenericMapper mapper;

    @Override
    public List<StateResponse> find(Map<String, String> parameters) {
        return stateRepository.findAll(filterWithParameters(parameters))
                .stream()
                .map(this::mapperDto)
                .toList();
    }

    @Override
    public StateResponse findOne(String id) {
        StateEntity state = stateRepository.findById(UUID.fromString(id))
                .orElseThrow(() -> new RestExceptionHandler(ApiCodes.API_CODE_404, HttpStatus.NOT_FOUND,
                        "State not found"));
        return mapperDto(state);
    }

    @Override
    public StateResponse save(StateRequest request) {
        CountryEntity country = countryRepository.findById(UUID.fromString(request.getCountryId()))
                .orElseThrow(() -> new RestExceptionHandler(ApiCodes.API_CODE_404, HttpStatus.NOT_FOUND,
                        "Country not found"));

        StateEntity newState = mapper.map(request, StateEntity.class);
        try {
            newState.setCountry(country);
            newState = stateRepository.save(newState);
        } catch (Exception e) {
            log.error("Error to save state {}", e.getMessage());
            throw new RestExceptionHandler(ApiCodes.API_CODE_500, HttpStatus.INTERNAL_SERVER_ERROR,
                    "Error to save state");
        }
        return mapperDto(newState);
    }

    @Override
    public StateResponse update(String id, StateRequest request) {
        throw new UnsupportedOperationException("Unimplemented method 'update'");
    }

    @Override
    public ResponseServerDto delete(String id) {
        StateEntity state = stateRepository.findById(UUID.fromString(id))
                .orElseThrow(() -> new RestExceptionHandler(ApiCodes.API_CODE_404, HttpStatus.NOT_FOUND,
                        "State not found"));
        try {
            state.setErased(true);
            stateRepository.save(state);
            return new ResponseServerDto("The state has been removed.", HttpStatus.ACCEPTED, true);
        } catch (Exception e) {
            log.error("Error to delete country ", e);
            return new ResponseServerDto("An error occurred while deleting the state", HttpStatus.METHOD_NOT_ALLOWED,
                    false);
        }
    }

    private StateResponse mapperDto(StateEntity source) {
        return mapper.map(source, StateResponse.class);
    }

    private Specification<StateEntity> filterWithParameters(Map<String, String> parameters) {
        return new StateSpecification().getSpecificationByFilters(parameters);
    }

    @Override
    public ResponseServerDto multiSaving(String countryId, List<String> stateList) {
        CountryEntity country = countryRepository.findById(UUID.fromString(countryId))
                .orElseThrow(() -> new RestExceptionHandler(ApiCodes.API_CODE_404, HttpStatus.NOT_FOUND,
                        "Country not found"));

        List<StateEntity> states = new ArrayList<>();
        stateList.stream().forEach(item -> states.add(StateEntity.builder()
                .name(item)
                .country(country)
                .build()));
        try {
            stateRepository.saveAll(states);
            return new ResponseServerDto("the states have been created.", HttpStatus.ACCEPTED, true);
        } catch (Exception e) {
            log.error("Error to save all states ", e);
            return new ResponseServerDto("An error occurred while creating the states", HttpStatus.METHOD_NOT_ALLOWED,
                    false);
        }
    }
}
