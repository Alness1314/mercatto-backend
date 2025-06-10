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
    @Transactional
    public TaxpayerResponse update(String id, TaxpayerRequest request) {
        // 1. Verificar que el taxpayer exista
        TaxpayerEntity existingTaxpayer = taxpayerRepository.findById(UUID.fromString(id))
                .orElseThrow(() -> new RestExceptionHandler(ApiCodes.API_CODE_404, HttpStatus.NOT_FOUND,
                        "Taxpayer not found"));

        log.info("contribuyente desde la DB encriptado: {}", existingTaxpayer);
        // debemos desencriptar la información
        decryptData(existingTaxpayer);
        // nunca actulizar la key de taxpayer guardarla en temporal
        String keyExistTaxpayer = existingTaxpayer.getDataKey();

        log.info("contribuyente desde la DB desencriptado: {}", existingTaxpayer);

        try {
            // 2. Actualizar datos básicos del taxpayer
            mapperUpdate.map(request, existingTaxpayer);

            // 3. Manejar Legal Representative primero (no requiere operaciones de BD)
            handleLegalRepresentative(request, existingTaxpayer);

            // 5. Encriptar el taxpayer antes de cualquier operación de BD

            // 6. Guardar el taxpayer encriptado primero
            existingTaxpayer = taxpayerRepository.save(existingTaxpayer);

            // 7. Actualizar dirección (si se proporciona) - después de guardar el taxpayer
            if (request.getAddress() != null) {
                String addressId = existingTaxpayer.getAddress() != null
                        ? existingTaxpayer.getAddress().getId().toString()
                        : null;

                // La actualización de la dirección se maneja por separado
                AddressResponse addressResponse = addressService.update(addressId, request.getAddress());
                AddressEntity addressEntity = addressRepository.findById(addressResponse.getId())
                        .orElseThrow(() -> new RuntimeException("Address not found after update"));

                // Actualizar la referencia en el taxpayer
                existingTaxpayer.setAddress(addressEntity);
                existingTaxpayer.setDataKey(keyExistTaxpayer);
                taxpayerRepository.save(existingTaxpayer); // Guardar solo la referencia
            }

            log.info("contribuyente editado encriptado guardado: {}", existingTaxpayer);

            // Desencriptar para la respuesta
            return mapperUpdate.map(existingTaxpayer, TaxpayerResponse.class);

        } catch (Exception e) {
            log.error("Error updating taxpayer with id {}: {}", id, e.getMessage(), e);
            throw new RestExceptionHandler(ApiCodes.API_CODE_500, HttpStatus.INTERNAL_SERVER_ERROR,
                    "Error updating taxpayer: " + e.getMessage());
        }
    }

    private void handleLegalRepresentative(TaxpayerRequest request,
            TaxpayerEntity taxpayer) {
        if (!"Moral".equalsIgnoreCase(request.getTypePerson()) ||
                request.getLegalRepresentative() == null) {
            taxpayer.setLegalRepresentative(null);
            return;
        }

        LegalRepresentativeEntity legalRep = taxpayer.getLegalRepresentative();

        // Caso 1: Nuevo representante legal
        if (legalRep == null) {
            log.info("Deberia entrar aqui por que no lleva un rep legal por defaul sino que se actualizara");
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
         TaxpayerEntity existingTaxpayer = taxpayerRepository.findById(UUID.fromString(id))
                .orElseThrow(() -> new RestExceptionHandler(ApiCodes.API_CODE_404, HttpStatus.NOT_FOUND,
                        "Taxpayer not found"));

        log.info("contribuyente desde la DB encriptado: {}", existingTaxpayer);
        // debemos desencriptar la información
        decryptData(existingTaxpayer);
        try {
            if(existingTaxpayer.getLegalRepresentative()!=null){
                existingTaxpayer.getLegalRepresentative().setErased(true);
            }
            existingTaxpayer.setErased(true);
            taxpayerRepository.save(existingTaxpayer);
            return new ResponseServerDto("Taxpayer deleted", HttpStatus.ACCEPTED, true);
        } catch (Exception e) {
            throw new RestExceptionHandler(ApiCodes.API_CODE_409, HttpStatus.CONFLICT,
                    "Error deleting taxpayer: " + e.getMessage());
        }
    }

    private TaxpayerResponse mapperDto(TaxpayerEntity source) {
        decryptData(source);
        return mapper.map(source, TaxpayerResponse.class);
    }

    private Specification<TaxpayerEntity> filterWithParameters(Map<String, String> parameters) {
        return new TaxpayerSpecification().getSpecificationByFilters(parameters);
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
