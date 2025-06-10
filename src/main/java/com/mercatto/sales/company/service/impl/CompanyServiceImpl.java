package com.mercatto.sales.company.service.impl;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.mercatto.sales.address.dto.response.AddressResponse;
import com.mercatto.sales.address.entity.AddressEntity;
import com.mercatto.sales.address.service.AddressService;
import com.mercatto.sales.common.api.ApiCodes;
import com.mercatto.sales.common.messages.Messages;
import com.mercatto.sales.common.model.ResponseServerDto;
import com.mercatto.sales.company.dto.request.CompanyRequest;
import com.mercatto.sales.company.dto.response.CompanyResponse;
import com.mercatto.sales.company.entity.CompanyEntity;
import com.mercatto.sales.company.repository.CompanyRepository;
import com.mercatto.sales.company.service.CompanyService;
import com.mercatto.sales.company.specification.CompanySpecification;
import com.mercatto.sales.config.GenericMapper;
import com.mercatto.sales.exceptions.RestExceptionHandler;
import com.mercatto.sales.files.entity.FileEntity;
import com.mercatto.sales.files.repository.FileRepository;
import com.mercatto.sales.taxpayer.entity.TaxpayerEntity;
import com.mercatto.sales.taxpayer.repository.TaxpayerRepository;
import com.mercatto.sales.utils.TextEncrypterUtil;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class CompanyServiceImpl implements CompanyService {
    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private AddressService addressService;

    @Autowired
    private FileRepository fileRepository;

    @Autowired
    private TaxpayerRepository taxpayerRepository;

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
            FileEntity imageFile = fileRepository.findById(UUID.fromString(request.getImageId()))
                    .orElseThrow(() -> new RestExceptionHandler(ApiCodes.API_CODE_404, HttpStatus.NOT_FOUND,
                            "image not found"));
            company.setImage(imageFile);
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
        // Find existing company
        CompanyEntity existingCompany = companyRepository.findById(UUID.fromString(id))
                .orElseThrow(() -> new RestExceptionHandler(ApiCodes.API_CODE_404, HttpStatus.NOT_FOUND,
                        "Company not found"));

        // Verify taxpayer exists if it's being updated
        TaxpayerEntity taxpayer = taxpayerRepository.findById(UUID.fromString(request.getTaxpayerId()))
                .orElseThrow(() -> new RestExceptionHandler(ApiCodes.API_CODE_404, HttpStatus.NOT_FOUND,
                        "Taxpayer not found"));

        // Update address if present
        if (request.getAddress() != null) {
            AddressResponse address = addressService.update(
                    existingCompany.getAddress().getId().toString(),
                    request.getAddress());
            existingCompany.setAddress(mapper.map(address, AddressEntity.class));
        }

        // Update image if present
        if (request.getImageId() != null) {
            FileEntity imageFile = fileRepository.findById(UUID.fromString(request.getImageId()))
                    .orElseThrow(() -> new RestExceptionHandler(ApiCodes.API_CODE_404, HttpStatus.NOT_FOUND,
                            "Image not found"));
            existingCompany.setImage(imageFile);
        } else {
            existingCompany.setImage(null); // Clear image if no imageId provided
        }

        // Update other fields
        mapperUpdate.map(request, existingCompany);
        existingCompany.setTaxpayer(taxpayer);

        try {
            existingCompany = companyRepository.save(existingCompany);
        } catch (Exception e) {
            log.error("Error updating company {}", e.getMessage());
            throw new RestExceptionHandler(ApiCodes.API_CODE_500, HttpStatus.INTERNAL_SERVER_ERROR,
                    "Error updating company");
        }

        return mapperDto(existingCompany);
    }

    @Override
    public ResponseServerDto delete(String id) {
        // Find existing company
        CompanyEntity existingCompany = companyRepository.findById(UUID.fromString(id))
                .orElseThrow(() -> new RestExceptionHandler(ApiCodes.API_CODE_404, HttpStatus.NOT_FOUND,
                        "Company not found"));
        try {
            existingCompany.setErased(true);
            companyRepository.save(existingCompany);
            return new ResponseServerDto(String.format(Messages.DELETE_ENTITY, id), HttpStatus.ACCEPTED, true);
        } catch (Exception e) {
            throw new RestExceptionHandler(ApiCodes.API_CODE_409, HttpStatus.CONFLICT,
                    String.format(Messages.ERROR_TO_SAVE_ENTITY, e.getMessage()));
        }

    }

    private Specification<CompanyEntity> filterWithParameters(Map<String, String> parameters) {
        return new CompanySpecification().getSpecificationByFilters(parameters);
    }

    private CompanyResponse mapperDto(CompanyEntity source) {
        decryptData(source.getTaxpayer());
        return mapper.map(source, CompanyResponse.class);
    }

    private void decryptData(TaxpayerEntity source) {
        var legalRep = source.getLegalRepresentative();

        if (legalRep != null && legalRep.getDataKey() != null) {
            String key = legalRep.getDataKey();
            legalRep.setRfc(TextEncrypterUtil.decrypt(legalRep.getRfc(), key));
            legalRep.setFullName(TextEncrypterUtil.decrypt(legalRep.getFullName(), key));
        }

        String dataKey = source.getDataKey();
        if (dataKey != null) {
            source.setCorporateReasonOrNaturalPerson(TextEncrypterUtil.decrypt(
                    source.getCorporateReasonOrNaturalPerson(), dataKey));
            source.setRfc(TextEncrypterUtil.decrypt(source.getRfc(), dataKey));
        }
    }
}
