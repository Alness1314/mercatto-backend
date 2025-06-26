package com.mercatto.sales.country.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.mercatto.sales.common.api.ApiCodes;
import com.mercatto.sales.common.keys.Filters;
import com.mercatto.sales.common.messages.Messages;
import com.mercatto.sales.common.model.ResponseServerDto;
import com.mercatto.sales.config.GenericMapper;
import com.mercatto.sales.country.dto.request.CountryRequest;
import com.mercatto.sales.country.dto.response.CountryResponse;
import com.mercatto.sales.country.entity.CountryEntity;
import com.mercatto.sales.country.repository.CountryRepository;
import com.mercatto.sales.country.service.CountryService;
import com.mercatto.sales.country.specification.CountrySpecification;
import com.mercatto.sales.exceptions.RestExceptionHandler;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class CountryServiceImpl implements CountryService {
    @Autowired
    private CountryRepository countryRepository;

    @Autowired
    private GenericMapper mapper;

    @Override
    public List<CountryResponse> find(Map<String, String> parameters) {
        return countryRepository.findAll(filterWithParameters(parameters))
                .stream()
                .map(this::mapperDto)
                .toList();
    }

    @Override
    public CountryResponse findOne(String id) {
        CountryEntity country = countryRepository.findOne(filterWithParameters(Map.of(Filters.KEY_ID, id)))
                .orElseThrow(() -> new RestExceptionHandler(ApiCodes.API_CODE_404, HttpStatus.NOT_FOUND,
                        String.format(Messages.NOT_FOUND, id)));
        return mapperDto(country);
    }

    @Override
    public CountryResponse save(CountryRequest request) {
        CountryEntity newCountry = mapper.map(request, CountryEntity.class);
        try {
            newCountry = countryRepository.save(newCountry);
        } catch (DataIntegrityViolationException ex) {
            log.error(Messages.LOG_ERROR_DATA_INTEGRITY, ex.getMessage(), ex);
            throw new RestExceptionHandler(ApiCodes.API_CODE_400, HttpStatus.BAD_REQUEST,
                    Messages.DATA_INTEGRITY);
        } catch (Exception e) {
            log.error(Messages.LOG_ERROR_TO_SAVE_ENTITY, e.getMessage(), e);
            throw new RestExceptionHandler(ApiCodes.API_CODE_500, HttpStatus.INTERNAL_SERVER_ERROR,
                    Messages.ERROR_ENTITY_SAVE);
        }
        return mapperDto(newCountry);
    }

    private CountryResponse mapperDto(CountryEntity source) {
        return mapper.map(source, CountryResponse.class);
    }

    private Specification<CountryEntity> filterWithParameters(Map<String, String> parameters) {
        return new CountrySpecification().getSpecificationByFilters(parameters);
    }

    @Override
    public ResponseServerDto multiSaving(List<CountryRequest> countryList) {
        try {

            List<CountryEntity> countries = new ArrayList<>();
            countryList.stream().forEach(item -> countries.add(CountryEntity.builder()
                    .name(item.getName())
                    .code(item.getCode())
                    .build()));
            countryRepository.saveAll(countries);
            return new ResponseServerDto(Messages.COUNTRY_CREATE, HttpStatus.ACCEPTED, true, null);
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
