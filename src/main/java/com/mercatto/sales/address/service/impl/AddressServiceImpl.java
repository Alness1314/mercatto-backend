package com.mercatto.sales.address.service.impl;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.mercatto.sales.address.dto.request.AddressRequest;
import com.mercatto.sales.address.dto.response.AddressResponse;
import com.mercatto.sales.address.entity.AddressEntity;
import com.mercatto.sales.address.repository.AddressRepository;
import com.mercatto.sales.address.service.AddressService;
import com.mercatto.sales.address.specification.AddressSpecification;
import com.mercatto.sales.cities.entity.CityEntity;
import com.mercatto.sales.cities.repository.CityRepository;
import com.mercatto.sales.common.api.ApiCodes;
import com.mercatto.sales.common.messages.Messages;
import com.mercatto.sales.common.model.ResponseServerDto;
import com.mercatto.sales.config.GenericMapper;
import com.mercatto.sales.country.entity.CountryEntity;
import com.mercatto.sales.country.repository.CountryRepository;
import com.mercatto.sales.exceptions.RestExceptionHandler;
import com.mercatto.sales.states.entity.StateEntity;
import com.mercatto.sales.states.repository.StateRepository;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class AddressServiceImpl implements AddressService {
    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private CountryRepository countryRepository;

    @Autowired
    private StateRepository stateRepository;

    @Autowired
    private CityRepository cityRepository;

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
    public List<AddressResponse> find(Map<String, String> parameters) {
        return addressRepository.findAll(filterWithParameters(parameters))
                .stream()
                .map(this::mapperDto)
                .toList();
    }

    @Override
    public AddressResponse findOne(String id) {
        AddressEntity address = addressRepository.findById(UUID.fromString(id))
                .orElseThrow(() -> new RestExceptionHandler(ApiCodes.API_CODE_404, HttpStatus.NOT_FOUND,
                        String.format(Messages.NOT_FOUND, id)));
        return mapperDto(address);
    }

    @Override
    public AddressResponse save(AddressRequest request) {
        CountryEntity country = countryRepository.findById(UUID.fromString(request.getCountryId()))
                .orElseThrow(() -> new RestExceptionHandler(ApiCodes.API_CODE_404, HttpStatus.NOT_FOUND,
                        String.format(Messages.NOT_FOUND, request.getCountryId())));

        StateEntity state = stateRepository.findById(UUID.fromString(request.getStateId()))
                .orElseThrow(() -> new RestExceptionHandler(ApiCodes.API_CODE_404, HttpStatus.NOT_FOUND,
                        String.format(Messages.NOT_FOUND, request.getStateId())));

        CityEntity city = cityRepository.findById(UUID.fromString(request.getCityId()))
                .orElseThrow(() -> new RestExceptionHandler(ApiCodes.API_CODE_404, HttpStatus.NOT_FOUND,
                        String.format(Messages.NOT_FOUND, request.getCityId())));
        AddressEntity address = mapper.map(request, AddressEntity.class);
        try {
            address.setCountry(country);
            address.setState(state);
            address.setCity(city);
            address = addressRepository.save(address);
        } catch (Exception e) {
            log.error("Error to save address {}", e.getMessage());
            throw new RestExceptionHandler(ApiCodes.API_CODE_500, HttpStatus.INTERNAL_SERVER_ERROR,
                    "Error to save address");
        }
        return mapperDto(address);
    }

    @Override
    public AddressResponse update(String id, AddressRequest request) {
        // Verificar que la dirección exista
        AddressEntity existingAddress = addressRepository.findById(UUID.fromString(id))
                .orElseThrow(() -> new RestExceptionHandler(ApiCodes.API_CODE_404, HttpStatus.NOT_FOUND,
                        String.format(Messages.NOT_FOUND, request.getCountryId())));

        // Validar y obtener las entidades relacionadas
        CountryEntity country = countryRepository.findById(UUID.fromString(request.getCountryId()))
                .orElseThrow(() -> new RestExceptionHandler(ApiCodes.API_CODE_404, HttpStatus.NOT_FOUND,
                        "Country not found"));

        StateEntity state = stateRepository.findById(UUID.fromString(request.getStateId()))
                .orElseThrow(() -> new RestExceptionHandler(ApiCodes.API_CODE_404, HttpStatus.NOT_FOUND,
                        "State not found"));

        CityEntity city = cityRepository.findById(UUID.fromString(request.getCityId()))
                .orElseThrow(() -> new RestExceptionHandler(ApiCodes.API_CODE_404, HttpStatus.NOT_FOUND,
                        "City not found"));

        try {
            // Actualizar los campos de la entidad existente
            mapperUpdate.map(request, existingAddress);

            // Establecer las relaciones
            existingAddress.setCountry(country);
            existingAddress.setState(state);
            existingAddress.setCity(city);

            // Guardar los cambios
            AddressEntity updatedAddress = addressRepository.save(existingAddress);

            return mapperDto(updatedAddress);
        } catch (Exception e) {
            log.error("Error updating address with id {}: {}", id, e.getMessage());
            throw new RestExceptionHandler(ApiCodes.API_CODE_500, HttpStatus.INTERNAL_SERVER_ERROR,
                    "Error updating address");
        }
    }

    @Override
    public ResponseServerDto delete(String id) {
        AddressEntity existingAddress = addressRepository.findById(UUID.fromString(id))
                .orElseThrow(() -> new RestExceptionHandler(ApiCodes.API_CODE_404, HttpStatus.NOT_FOUND,
                        String.format(Messages.NOT_FOUND, id)));
        try {
            existingAddress.setErased(true);
            addressRepository.save(existingAddress);
            return new ResponseServerDto(String.format(Messages.DELETE_ENTITY, id), HttpStatus.ACCEPTED, true);
        } catch (Exception e) {
            throw new RestExceptionHandler(ApiCodes.API_CODE_409, HttpStatus.CONFLICT,
                    String.format(Messages.ERROR_TO_SAVE_ENTITY, e.getMessage()));
        }
    }

    private AddressResponse mapperDto(AddressEntity source) {
        return mapper.map(source, AddressResponse.class);
    }

    private Specification<AddressEntity> filterWithParameters(Map<String, String> parameters) {
        return new AddressSpecification().getSpecificationByFilters(parameters);
    }

}
