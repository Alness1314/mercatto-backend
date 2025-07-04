package com.mercatto.sales.address.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
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
import com.mercatto.sales.utils.ErrorLogger;
import com.mercatto.sales.utils.UUIDHandler;

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

    @Override
    public List<AddressResponse> find(Map<String, String> parameters) {
        return addressRepository.findAll(filterWithParameters(parameters))
                .stream()
                .map(this::mapperDto)
                .toList();
    }

    @Override
    public AddressResponse findOne(String id) {
        AddressEntity address = addressRepository.findById(UUIDHandler.toUUID(id))
                .orElseThrow(() -> new RestExceptionHandler(ApiCodes.API_CODE_404, HttpStatus.NOT_FOUND,
                        String.format(Messages.NOT_FOUND, id)));
        return mapperDto(address);
    }

    @Override
    public AddressResponse save(AddressRequest request) {
        CountryEntity country = countryRepository.findById(UUIDHandler.toUUID(request.getCountryId()))
                .orElseThrow(() -> new RestExceptionHandler(ApiCodes.API_CODE_404, HttpStatus.NOT_FOUND,
                        String.format(Messages.NOT_FOUND, request.getCountryId())));

        StateEntity state = stateRepository.findById(UUIDHandler.toUUID(request.getStateId()))
                .orElseThrow(() -> new RestExceptionHandler(ApiCodes.API_CODE_404, HttpStatus.NOT_FOUND,
                        String.format(Messages.NOT_FOUND, request.getStateId())));

        CityEntity city = cityRepository.findById(UUIDHandler.toUUID(request.getCityId()))
                .orElseThrow(() -> new RestExceptionHandler(ApiCodes.API_CODE_404, HttpStatus.NOT_FOUND,
                        String.format(Messages.NOT_FOUND, request.getCityId())));
        AddressEntity address = mapper.map(request, AddressEntity.class);
        try {
            address.setCountry(country);
            address.setState(state);
            address.setCity(city);
            address = addressRepository.save(address);
        } catch (DataIntegrityViolationException ex) {
            ErrorLogger.logError(ex);
            throw new RestExceptionHandler(ApiCodes.API_CODE_400, HttpStatus.BAD_REQUEST,
                    Messages.DATA_INTEGRITY);
        } catch (Exception e) {
            ErrorLogger.logError(e);
            throw new RestExceptionHandler(ApiCodes.API_CODE_500, HttpStatus.INTERNAL_SERVER_ERROR,
                    Messages.ERROR_ENTITY_SAVE);
        }
        return mapperDto(address);
    }

    @Override
    public AddressResponse update(String id, AddressRequest request) {
        // Verificar que la dirección exista
        AddressEntity existingAddress = addressRepository.findById(UUIDHandler.toUUID(id))
                .orElseThrow(() -> new RestExceptionHandler(ApiCodes.API_CODE_404, HttpStatus.NOT_FOUND,
                        String.format(Messages.NOT_FOUND, request.getCountryId())));

        // Validar y obtener las entidades relacionadas
        CountryEntity country = countryRepository.findById(UUIDHandler.toUUID(request.getCountryId()))
                .orElseThrow(() -> new RestExceptionHandler(ApiCodes.API_CODE_404, HttpStatus.NOT_FOUND,
                        String.format(Messages.NOT_FOUND, request.getCountryId())));

        StateEntity state = stateRepository.findById(UUIDHandler.toUUID(request.getStateId()))
                .orElseThrow(() -> new RestExceptionHandler(ApiCodes.API_CODE_404, HttpStatus.NOT_FOUND,
                        String.format(Messages.NOT_FOUND, request.getStateId())));

        CityEntity city = cityRepository.findById(UUIDHandler.toUUID(request.getCityId()))
                .orElseThrow(() -> new RestExceptionHandler(ApiCodes.API_CODE_404, HttpStatus.NOT_FOUND,
                        String.format(Messages.NOT_FOUND, request.getCityId())));

        try {
            // Actualizar los campos de la entidad existente
            mapper.map(request, existingAddress);

            // Establecer las relaciones
            existingAddress.setCountry(country);
            existingAddress.setState(state);
            existingAddress.setCity(city);

            // Guardar los cambios
            AddressEntity updatedAddress = addressRepository.save(existingAddress);

            return mapperDto(updatedAddress);
        } catch (DataIntegrityViolationException ex) {
            ErrorLogger.logError(ex);
            throw new RestExceptionHandler(ApiCodes.API_CODE_400, HttpStatus.BAD_REQUEST,
                    Messages.DATA_INTEGRITY);
        } catch (Exception e) {
            ErrorLogger.logError(e);
            throw new RestExceptionHandler(ApiCodes.API_CODE_500, HttpStatus.INTERNAL_SERVER_ERROR,
                    Messages.ERROR_ENTITY_UPDATE);
        }
    }

    @Override
    public ResponseServerDto delete(String id) {
        AddressEntity existingAddress = addressRepository.findById(UUIDHandler.toUUID(id))
                .orElseThrow(() -> new RestExceptionHandler(ApiCodes.API_CODE_404, HttpStatus.NOT_FOUND,
                        String.format(Messages.NOT_FOUND, id)));
        try {
            existingAddress.setErased(true);
            addressRepository.save(existingAddress);
            return new ResponseServerDto(String.format(Messages.ENTITY_DELETE, id), HttpStatus.ACCEPTED, true);
        } catch (DataIntegrityViolationException ex) {
            ErrorLogger.logError(ex);
            throw new RestExceptionHandler(ApiCodes.API_CODE_400, HttpStatus.BAD_REQUEST,
                    Messages.DATA_INTEGRITY);
        } catch (Exception e) {
            ErrorLogger.logError(e);
            throw new RestExceptionHandler(ApiCodes.API_CODE_409, HttpStatus.CONFLICT,
                    String.format(Messages.ERROR_ENTITY_DELETE, e.getMessage()));
        }
    }

    private AddressResponse mapperDto(AddressEntity source) {
        return mapper.map(source, AddressResponse.class);
    }

    private Specification<AddressEntity> filterWithParameters(Map<String, String> parameters) {
        return new AddressSpecification().getSpecificationByFilters(parameters);
    }

}
