package com.mercatto.sales.unit.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
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
                        "Category not found"));
        return mapperDto(um);
    }

    @Override
    public UnitMeasurementResp save(String companyId, UnitMeasurementReq request) {
        CompanyEntity company = companyRepository.findById(UUID.fromString(companyId))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Company not found"));
        UnitMeasurement um = mapper.map(request, UnitMeasurement.class);
        try {
            um.setCompany(company);
            um = unitMeasurementRepo.save(um);
        } catch (Exception e) {
            log.error("Error to save category {}", e.getMessage());
            throw new RestExceptionHandler(ApiCodes.API_CODE_500, HttpStatus.INTERNAL_SERVER_ERROR,
                    "Error to save category");
        }
        return mapperDto(um);
    }

    @Override
    public UnitMeasurementResp update(String companyId, String id, UnitMeasurementReq request) {
        // Verificar que la compañía existe
        CompanyEntity company = companyRepository.findById(UUID.fromString(companyId))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Company not found"));

        // Buscar la unidad de medida existente
        Map<String, String> params = Map.of(Filters.KEY_ID, id, Filters.KEY_COMPANY_ID, companyId);
        UnitMeasurement existingUnit = unitMeasurementRepo.findOne(filterWithParameters(params))
                .orElseThrow(() -> new RestExceptionHandler(ApiCodes.API_CODE_404, HttpStatus.NOT_FOUND,
                        "Unit of measurement not found for this company"));

        try {
            // Actualizar los campos de la entidad existente con los valores del request
            mapperUpdate.map(request, existingUnit);
            existingUnit.setCompany(company); // Mantener la relación con la compañía

            // Guardar los cambios
            UnitMeasurement updatedUnit = unitMeasurementRepo.save(existingUnit);
            return mapperDto(updatedUnit);
        } catch (Exception e) {
            log.error("Error updating unit of measurement {}", e.getMessage());
            throw new RestExceptionHandler(ApiCodes.API_CODE_500, HttpStatus.INTERNAL_SERVER_ERROR,
                    "Error updating unit of measurement");
        }
    }

    @Override
    public ResponseServerDto delete(String companyId, String id) {
        Map<String, String> params = Map.of(Filters.KEY_ID, id, Filters.KEY_COMPANY_ID, companyId);
        UnitMeasurement existingUnit = unitMeasurementRepo.findOne(filterWithParameters(params))
                .orElseThrow(() -> new RestExceptionHandler(ApiCodes.API_CODE_404, HttpStatus.NOT_FOUND,
                        "Unit of measurement not found for this company"));
        try {
            existingUnit.setErased(true);
            unitMeasurementRepo.save(existingUnit);
            return new ResponseServerDto(String.format(Messages.DELETE_ENTITY, id), HttpStatus.ACCEPTED, true);
        } catch (Exception e) {
            throw new RestExceptionHandler(ApiCodes.API_CODE_409, HttpStatus.CONFLICT,
                    String.format(Messages.ERROR_TO_SAVE_ENTITY, e.getMessage()));
        }
    }

    private UnitMeasurementResp mapperDto(UnitMeasurement source) {
        return mapper.map(source, UnitMeasurementResp.class);
    }

    public Specification<UnitMeasurement> filterWithParameters(Map<String, String> parameters) {
        return new UnitMeasurementSpec().getSpecificationByFilters(parameters);
    }

}
