package com.mercatto.sales.unit.service.impl;

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

import com.mercatto.sales.common.api.ApiCodes;
import com.mercatto.sales.common.keys.Filters;
import com.mercatto.sales.common.messages.Messages;
import com.mercatto.sales.common.model.ResponseServerDto;
import com.mercatto.sales.company.entity.CompanyEntity;
import com.mercatto.sales.company.repository.CompanyRepository;
import com.mercatto.sales.config.GenericMapper;
import com.mercatto.sales.exceptions.RestExceptionHandler;
import com.mercatto.sales.unit.dto.request.UnitMeasurementReq;
import com.mercatto.sales.unit.dto.response.UnitMeasurementResp;
import com.mercatto.sales.unit.entity.UnitMeasurement;
import com.mercatto.sales.unit.repository.UnitMeasurementRepo;
import com.mercatto.sales.unit.service.UnitMeasurementService;
import com.mercatto.sales.unit.specification.UnitMeasurementSpec;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class UnitMeasurementServiceImp implements UnitMeasurementService {
    @Autowired
    private UnitMeasurementRepo unitMeasurementRepo;

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
    public List<UnitMeasurementResp> find(String companyId, Map<String, String> params) {
        Map<String, String> paramsNew = new HashMap<>(params);
        paramsNew.put(Filters.KEY_COMPANY_ID, companyId);
        Specification<UnitMeasurement> specification = filterWithParameters(paramsNew);
        return unitMeasurementRepo.findAll(specification)
                .stream()
                .map(this::mapperDto)
                .toList();
    }

    @Override
    public UnitMeasurementResp findOne(String companyId, String id) {
        Map<String, String> params = Map.of(Filters.KEY_ID, id, Filters.KEY_COMPANY_ID, companyId);
        UnitMeasurement um = unitMeasurementRepo.findOne(filterWithParameters(params))
                .orElseThrow(() -> new RestExceptionHandler(ApiCodes.API_CODE_404, HttpStatus.NOT_FOUND,
                        String.format(Messages.NOT_FOUND, id)));
        return mapperDto(um);
    }

    @Override
    public UnitMeasurementResp save(String companyId, UnitMeasurementReq request) {
        CompanyEntity company = companyRepository.findById(UUID.fromString(companyId))
                .orElseThrow(() -> new RestExceptionHandler(ApiCodes.API_CODE_404, HttpStatus.NOT_FOUND,
                        String.format(Messages.NOT_FOUND, companyId)));
        UnitMeasurement um = mapper.map(request, UnitMeasurement.class);
        try {
            um.setCompany(company);
            um = unitMeasurementRepo.save(um);
        } catch (DataIntegrityViolationException ex) {
            log.error(Messages.LOG_ERROR_DATA_INTEGRITY, ex.getMessage(), ex);
            throw new RestExceptionHandler(ApiCodes.API_CODE_400, HttpStatus.BAD_REQUEST,
                    Messages.DATA_INTEGRITY);
        } catch (Exception e) {
            log.error(Messages.LOG_ERROR_TO_SAVE_ENTITY, e.getMessage(), e);
            throw new RestExceptionHandler(ApiCodes.API_CODE_500, HttpStatus.INTERNAL_SERVER_ERROR,
                    Messages.ERROR_ENTITY_SAVE);
        }
        return mapperDto(um);
    }

    @Override
    public UnitMeasurementResp update(String companyId, String id, UnitMeasurementReq request) {
        // Verificar que la compañía existe
        CompanyEntity company = companyRepository.findById(UUID.fromString(companyId))
                .orElseThrow(() -> new RestExceptionHandler(ApiCodes.API_CODE_404, HttpStatus.NOT_FOUND,
                        String.format(Messages.NOT_FOUND, companyId)));

        // Buscar la unidad de medida existente
        Map<String, String> params = Map.of(Filters.KEY_ID, id, Filters.KEY_COMPANY_ID, companyId);
        UnitMeasurement existingUnit = unitMeasurementRepo.findOne(filterWithParameters(params))
                .orElseThrow(() -> new RestExceptionHandler(ApiCodes.API_CODE_404, HttpStatus.NOT_FOUND,
                        String.format(Messages.NOT_FOUND, id)));

        try {
            // Actualizar los campos de la entidad existente con los valores del request
            mapperUpdate.map(request, existingUnit);
            existingUnit.setCompany(company); // Mantener la relación con la compañía

            // Guardar los cambios
            UnitMeasurement updatedUnit = unitMeasurementRepo.save(existingUnit);
            return mapperDto(updatedUnit);
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
        Map<String, String> params = Map.of(Filters.KEY_ID, id, Filters.KEY_COMPANY_ID, companyId);
        UnitMeasurement existingUnit = unitMeasurementRepo.findOne(filterWithParameters(params))
                .orElseThrow(() -> new RestExceptionHandler(ApiCodes.API_CODE_404, HttpStatus.NOT_FOUND,
                        String.format(Messages.NOT_FOUND, id)));
        try {
            existingUnit.setErased(true);
            unitMeasurementRepo.save(existingUnit);
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

    private UnitMeasurementResp mapperDto(UnitMeasurement source) {
        return mapper.map(source, UnitMeasurementResp.class);
    }

    public Specification<UnitMeasurement> filterWithParameters(Map<String, String> parameters) {
        return new UnitMeasurementSpec().getSpecificationByFilters(parameters);
    }

}
