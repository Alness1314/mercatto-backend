package com.mercatto.sales.taxpayer.service.impl;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.mercatto.sales.address.dto.response.AddressResponse;
import com.mercatto.sales.address.entity.AddressEntity;
import com.mercatto.sales.address.repository.AddressRepository;
import com.mercatto.sales.address.service.AddressService;
import com.mercatto.sales.common.api.ApiCodes;
import com.mercatto.sales.common.model.ResponseServerDto;

import com.mercatto.sales.config.GenericMapper;
import com.mercatto.sales.exceptions.RestExceptionHandler;
import com.mercatto.sales.taxpayer.dto.request.TaxpayerRequest;
import com.mercatto.sales.taxpayer.dto.response.TaxpayerResponse;
import com.mercatto.sales.taxpayer.entity.LegalRepresentativeEntity;
import com.mercatto.sales.taxpayer.entity.TaxpayerEntity;
import com.mercatto.sales.taxpayer.repository.TaxpayerRepository;
import com.mercatto.sales.taxpayer.service.TaxpayerService;
import com.mercatto.sales.taxpayer.specification.TaxpayerSpecification;
import com.mercatto.sales.utils.TextEncrypterUtil;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class TaxpayerServiceImpl implements TaxpayerService {
    @Autowired
    private AddressService addressService;

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private TaxpayerRepository taxpayerRepository;

    @Autowired
    private GenericMapper mapper;

    @Override
    public List<TaxpayerResponse> find(Map<String, String> parameters) {
        return taxpayerRepository.findAll(filterWithParameters(parameters))
                .stream()
                .map(this::mapperDto)
                .toList();
    }

    @Override
    public TaxpayerResponse findOne(String id) {
        TaxpayerEntity taxpayer = taxpayerRepository.findById(UUID.fromString(id))
                .orElseThrow(() -> new RestExceptionHandler(ApiCodes.API_CODE_404, HttpStatus.NOT_FOUND,
                        "taxpayer not found"));
        return mapperDto(taxpayer);
    }

    @Override
    public TaxpayerResponse save(TaxpayerRequest request) {
        TaxpayerEntity taxpayer = mapper.map(request, TaxpayerEntity.class);

        try {
            // Guardar dirección del contribuyente primero
            AddressResponse addressResponse = addressService.save(request.getAddress());
            AddressEntity addressEntity = addressRepository.findById(addressResponse.getId())
                    .orElseThrow(() -> new RuntimeException("Address not found after save"));
            taxpayer.setAddress(addressEntity);

            // Guardar Legal Representative si aplica
            if (request.getTypePerson().equalsIgnoreCase("Moral") && request.getLegalRepresentative() != null) {
                LegalRepresentativeEntity legalRep = mapper.map(request.getLegalRepresentative(),
                        LegalRepresentativeEntity.class);

                SecretKey key = TextEncrypterUtil.generateKey();
                String keyString = TextEncrypterUtil.keyToString(key);
                //log.info("Key generated: {}", keyString);
                //legalRep.setFullName(TextEncrypterUtil.encrypt(legalRep.getFullName(), keyString));
                //log.info("Encrypted full name: {}", legalRep.getFullName());
                //legalRep.setRfc(TextEncrypterUtil.encrypt(legalRep.getRfc(), keyString));
                //log.info("Encrypted RFC: {}", legalRep.getRfc());
                legalRep.setDataKey(keyString);

                legalRep.setTaxpayer(taxpayer);
                taxpayer.setLegalRepresentative(legalRep);
            } else {
                taxpayer.setLegalRepresentative(null);
            }

            // Finalmente, guardar taxpayer
            SecretKey key = TextEncrypterUtil.generateKey();
            String keyString = TextEncrypterUtil.keyToString(key);
            //taxpayer.setCorporateReasonOrNaturalPerson(
                    //TextEncrypterUtil.encrypt(taxpayer.getCorporateReasonOrNaturalPerson(), keyString));
            //log.info("Encrypted corporate reason or natural person: {}", taxpayer.getCorporateReasonOrNaturalPerson());
            //taxpayer.setRfc(TextEncrypterUtil.encrypt(taxpayer.getRfc(), keyString));
            taxpayer.setDataKey(keyString);

            taxpayer = taxpayerRepository.save(taxpayer);
        } catch (Exception e) {
            log.error("Error to save taxpayer {}", e.getMessage());
            e.printStackTrace();
            throw new RestExceptionHandler(ApiCodes.API_CODE_500, HttpStatus.INTERNAL_SERVER_ERROR,
                    "Error to save taxpayer");
        }

        return mapperDto(taxpayer);
    }

    @Override
    public TaxpayerResponse update(String id, TaxpayerRequest request) {
        throw new UnsupportedOperationException("Unimplemented method 'update'");
    }

    @Override
    public ResponseServerDto delete(String id) {
        throw new UnsupportedOperationException("Unimplemented method 'delete'");
    }

    private TaxpayerResponse mapperDto(TaxpayerEntity source) {
        return mapper.map(source, TaxpayerResponse.class);
    }

    private Specification<TaxpayerEntity> filterWithParameters(Map<String, String> parameters) {
        return new TaxpayerSpecification().getSpecificationByFilters(parameters);
    }

}
