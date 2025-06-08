package com.mercatto.sales.company.service.impl;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.mercatto.sales.address.dto.response.AddressResponse;
import com.mercatto.sales.address.entity.AddressEntity;
import com.mercatto.sales.address.service.AddressService;
import com.mercatto.sales.common.api.ApiCodes;
import com.mercatto.sales.common.model.ResponseServerDto;
import com.mercatto.sales.company.dto.request.CompanyRequest;
import com.mercatto.sales.company.dto.response.CompanyResponse;
import com.mercatto.sales.company.entity.CompanyEntity;
import com.mercatto.sales.company.repository.CompanyRepository;
import com.mercatto.sales.company.service.CompanyService;
import com.mercatto.sales.company.specification.CompanySpecification;
import com.mercatto.sales.config.GenericMapper;
import com.mercatto.sales.exceptions.RestExceptionHandler;
import com.mercatto.sales.files.dto.FileResponse;
import com.mercatto.sales.files.entity.FileEntity;
import com.mercatto.sales.files.service.FileService;
import com.mercatto.sales.taxpayer.entity.TaxpayerEntity;
import com.mercatto.sales.taxpayer.repository.TaxpayerRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class CompanyServiceImpl implements CompanyService{
     @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private AddressService addressService;

    @Autowired
    private FileService fileService;

    @Autowired
    private TaxpayerRepository taxpayerRepository;

    @Autowired
    private GenericMapper mapper;

    @Override
    public List<CompanyResponse> find(Map<String, String> parameters) {
        return companyRepository.findAll(filterWithParameters(parameters))
                .stream()
                .map(this::mapperDto)
                .toList();
    }

    @Override
    public CompanyResponse findOne(String id) {
        UUID idUUID = UUID.fromString(id);
        log.info("UUID id: {}", idUUID);
        CompanyEntity company = companyRepository.findById(idUUID)
                .orElseThrow(() -> new RestExceptionHandler(ApiCodes.API_CODE_404, HttpStatus.NOT_FOUND,
                        "company not found"));
        return mapperDto(company);
    }

    @Override
    public CompanyResponse save(CompanyRequest request) {
        TaxpayerEntity taxpayer = taxpayerRepository.findById(UUID.fromString(request.getTaxpayerId()))
                .orElseThrow(() -> new RestExceptionHandler(ApiCodes.API_CODE_404, HttpStatus.NOT_FOUND,
                        "taxpayer not found"));

        CompanyEntity company = mapper.map(request, CompanyEntity.class);
        AddressResponse address = addressService.save(request.getAddress());
        company.setAddress(mapper.map(address, AddressEntity.class));

        if (request.getImageId() != null) {
            log.info("ingreso a imagen");
            FileResponse imageFile = fileService.findOne(request.getImageId());
            company.setImage(mapper.map(imageFile, FileEntity.class));
        }
        company.setTaxpayer(taxpayer);
        try {
            company = companyRepository.save(company);
        } catch (Exception e) {
            log.error("Error to save company {}", e.getMessage());
            throw new RestExceptionHandler(ApiCodes.API_CODE_500, HttpStatus.INTERNAL_SERVER_ERROR,
                    "Error to save company");
        }
        return mapperDto(company);
    }

    @Override
    public CompanyResponse update(String id, CompanyRequest request) {
        throw new UnsupportedOperationException("Unimplemented method 'update'");
    }

    @Override
    public ResponseServerDto delete(String id) {
        throw new UnsupportedOperationException("Unimplemented method 'delete'");
    }

    private Specification<CompanyEntity> filterWithParameters(Map<String, String> parameters) {
        return new CompanySpecification().getSpecificationByFilters(parameters);
    }

    private CompanyResponse mapperDto(CompanyEntity source) {
        return mapper.map(source, CompanyResponse.class);
    }
}
