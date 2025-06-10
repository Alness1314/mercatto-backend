package com.mercatto.sales.taxpayer.service.impl;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.crypto.SecretKey;

import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
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

import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
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

    ModelMapper mapperUpdate = new ModelMapper();

    @PostConstruct
    private void init() {
        mapperUpdate.getConfiguration()
                .setSkipNullEnabled(true)
                .setFieldMatchingEnabled(true)
                .setMatchingStrategy(MatchingStrategies.STRICT);
    }

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
                legalRep.setDataKey(keyString);

                legalRep.setTaxpayer(taxpayer);
                taxpayer.setLegalRepresentative(legalRep);
            } else {
                taxpayer.setLegalRepresentative(null);
            }

            // Finalmente, guardar taxpayer
            SecretKey key = TextEncrypterUtil.generateKey();
            String keyString = TextEncrypterUtil.keyToString(key);
            taxpayer.setDataKey(keyString);
            // antes de guardar encriptar

            taxpayer = taxpayerRepository.save(encryptData(taxpayer));
        } catch (Exception e) {
            log.error("Error to save taxpayer {}", e.getMessage());
            e.printStackTrace();
            throw new RestExceptionHandler(ApiCodes.API_CODE_500, HttpStatus.INTERNAL_SERVER_ERROR,
                    "Error to save taxpayer");
        }

        return mapperDto(taxpayer);
    }

    @Override
    //@Transactional
    public TaxpayerResponse update(String id, TaxpayerRequest request) {
        // 1. Verificar que el taxpayer exista
        String dataKeyTax = null;
        TaxpayerEntity existingTaxpayer = taxpayerRepository.findById(UUID.fromString(id))
                .orElseThrow(() -> new RestExceptionHandler(ApiCodes.API_CODE_404, HttpStatus.NOT_FOUND,
                        "Taxpayer not found"));
        if(existingTaxpayer.getDataKey()!=null){
            log.info("conservamos la data key original: {}", existingTaxpayer.getDataKey());
            dataKeyTax = existingTaxpayer.getDataKey();
        }
        

        log.info("contribuyente desde la DB encriptado: {}", existingTaxpayer);

        try {
            TaxpayerEntity existngTaxpayerDecrypt = decryptData(existingTaxpayer);
            log.info("contribuyente desde la DB desencriptado: {}", existngTaxpayerDecrypt);
            // 2. Actualizar datos básicos del taxpayer
            mapperUpdate.map(request, existngTaxpayerDecrypt);

            // 3. Actualizar dirección (si se proporciona)
            if (request.getAddress() != null) {
                UUID addressId = existngTaxpayerDecrypt.getAddress() != null
                        ? existngTaxpayerDecrypt.getAddress().getId()
                        : null;

                AddressResponse addressResponse = addressService.update(addressId.toString(), request.getAddress());
                AddressEntity addressEntity = addressRepository.findById(addressResponse.getId())
                        .orElseThrow(() -> new RuntimeException("Address not found after update"));
                existngTaxpayerDecrypt.setAddress(addressEntity);
            }

            // 4. Manejar Legal Representative
            handleLegalRepresentative(request, existngTaxpayerDecrypt);

            // 5. Asegurar data_key del taxpayer
            if (existngTaxpayerDecrypt.getDataKey() == null || existngTaxpayerDecrypt.getDataKey().isEmpty()) {
                SecretKey key = TextEncrypterUtil.generateKey();
                existngTaxpayerDecrypt.setDataKey(TextEncrypterUtil.keyToString(key));
            }

            // 6. Guardar cambios
            log.info("contribuyente editado desencriptado antes de guardar: {}", existngTaxpayerDecrypt);
            existingTaxpayer = encryptData(existngTaxpayerDecrypt);
            log.info("contribuyente editado encriptado antes de guardar: {}", existingTaxpayer);
            existingTaxpayer.setDataKey(dataKeyTax);
            TaxpayerEntity existngTaxpayerEncrypt = taxpayerRepository.save(existingTaxpayer);

            log.info("contribuyente editado encriptado guardado: {}", existngTaxpayerEncrypt);
            return mapperUpdate.map(decryptData(existngTaxpayerEncrypt), TaxpayerResponse.class);

        } catch (Exception e) {
            log.error("Error updating taxpayer with id {}: {}", id, e.getMessage(), e);
            throw new RestExceptionHandler(ApiCodes.API_CODE_500, HttpStatus.INTERNAL_SERVER_ERROR,
                    "Error updating taxpayer: " + e.getMessage());
        }
    }

    private void handleLegalRepresentative(TaxpayerRequest request, TaxpayerEntity taxpayer) {
        if (!"Moral".equalsIgnoreCase(request.getTypePerson()) ||
                request.getLegalRepresentative() == null) {
            taxpayer.setLegalRepresentative(null);
            return;
        }

        LegalRepresentativeEntity legalRep = taxpayer.getLegalRepresentative();

        // Caso 1: Nuevo representante legal
        if (legalRep == null) {
            legalRep = new LegalRepresentativeEntity();
            mapperUpdate.map(request.getLegalRepresentative(), legalRep);

            SecretKey key = TextEncrypterUtil.generateKey();
            legalRep.setDataKey(TextEncrypterUtil.keyToString(key));
            legalRep.setTaxpayer(taxpayer);
            taxpayer.setLegalRepresentative(legalRep);
        }
        // Caso 2: Actualizar representante existente
        else {
            String existingKey = legalRep.getDataKey(); // Conservar la clave existente

            mapperUpdate.map(request.getLegalRepresentative(), legalRep);
            legalRep.setDataKey(
                    existingKey != null ? existingKey : TextEncrypterUtil.keyToString(TextEncrypterUtil.generateKey()));

            legalRep.setTaxpayer(taxpayer); // Reforzar la relación
        }
    }

    @Override
    public ResponseServerDto delete(String id) {
        throw new UnsupportedOperationException("Unimplemented method 'delete'");
    }

    private TaxpayerResponse mapperDto(TaxpayerEntity source) {
        return mapper.map(decryptData(source), TaxpayerResponse.class);
    }

    private Specification<TaxpayerEntity> filterWithParameters(Map<String, String> parameters) {
        return new TaxpayerSpecification().getSpecificationByFilters(parameters);
    }

    private TaxpayerEntity encryptData(TaxpayerEntity source) {
        TaxpayerEntity encrypted = new TaxpayerEntity();
        // Copiar todos los campos básicos
        encrypted.setId(source.getId());
        encrypted.setTypePerson(source.getTypePerson());
        encrypted.setDataKey(source.getDataKey());
        encrypted.setCreateAt(source.getCreateAt());
        encrypted.setUpdateAt(source.getUpdateAt());
        encrypted.setErased(source.getErased());
        encrypted.setAddress(source.getAddress());

        // Encriptar campos sensibles
        encrypted.setCorporateReasonOrNaturalPerson(
                TextEncrypterUtil.encrypt(source.getCorporateReasonOrNaturalPerson(), source.getDataKey()));
        encrypted.setRfc(TextEncrypterUtil.encrypt(source.getRfc(), source.getDataKey()));

        // Manejar LegalRepresentative
        if (source.getLegalRepresentative() != null) {
            LegalRepresentativeEntity lr = new LegalRepresentativeEntity();
            lr.setId(source.getLegalRepresentative().getId());
            lr.setDataKey(source.getLegalRepresentative().getDataKey());
            lr.setErased(source.getLegalRepresentative().getErased());
            lr.setTaxpayer(encrypted);

            lr.setFullName(TextEncrypterUtil.encrypt(
                    source.getLegalRepresentative().getFullName(),
                    source.getLegalRepresentative().getDataKey()));
            lr.setRfc(TextEncrypterUtil.encrypt(
                    source.getLegalRepresentative().getRfc(),
                    source.getLegalRepresentative().getDataKey()));

            encrypted.setLegalRepresentative(lr);
        }

        return encrypted;
    }

    private TaxpayerEntity decryptData(TaxpayerEntity source) {
        String encCorporate = TextEncrypterUtil.decrypt(source.getCorporateReasonOrNaturalPerson(),
                source.getDataKey());
        String rfcCorporate = TextEncrypterUtil.decrypt(source.getRfc(), source.getDataKey());
        source.setCorporateReasonOrNaturalPerson(encCorporate);
        source.setRfc(rfcCorporate);
        if (source.getLegalRepresentative() != null) {
            String legalRepRfc = TextEncrypterUtil.decrypt(source.getLegalRepresentative().getRfc(),
                    source.getLegalRepresentative().getDataKey());
            String legalRepName = TextEncrypterUtil.decrypt(source.getLegalRepresentative().getFullName(),
                    source.getLegalRepresentative().getDataKey());
            source.getLegalRepresentative().setFullName(legalRepName);
            source.getLegalRepresentative().setRfc(legalRepRfc);
        }

        return source;
    }
}
