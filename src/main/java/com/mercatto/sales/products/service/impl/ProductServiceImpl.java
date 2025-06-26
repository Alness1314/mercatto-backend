package com.mercatto.sales.products.service.impl;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.mercatto.sales.categories.entity.CategoryEntity;
import com.mercatto.sales.categories.repository.CategoryRepository;
import com.mercatto.sales.common.api.ApiCodes;
import com.mercatto.sales.common.keys.Filters;
import com.mercatto.sales.common.messages.Messages;
import com.mercatto.sales.common.model.ResponseServerDto;
import com.mercatto.sales.company.entity.CompanyEntity;
import com.mercatto.sales.company.repository.CompanyRepository;
import com.mercatto.sales.config.GenericMapper;
import com.mercatto.sales.exceptions.RestExceptionHandler;
import com.mercatto.sales.files.entity.FileEntity;
import com.mercatto.sales.files.repository.FileRepository;
import com.mercatto.sales.products.dto.request.ProductRequest;
import com.mercatto.sales.products.dto.response.ProductResponse;
import com.mercatto.sales.products.entity.ProductEntity;
import com.mercatto.sales.products.repository.ProductRepository;
import com.mercatto.sales.products.service.ProductService;
import com.mercatto.sales.products.specification.ProductSpecification;
import com.mercatto.sales.transactions.dto.request.SalesRequest;
import com.mercatto.sales.transactions.entity.SalesEntity;
import com.mercatto.sales.unit.entity.UnitMeasurement;
import com.mercatto.sales.unit.repository.UnitMeasurementRepo;
import com.mercatto.sales.utils.DateTimeUtils;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ProductServiceImpl implements ProductService {
    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private UnitMeasurementRepo unitMeasurementRepo;

    @Autowired
    private GenericMapper mapper;

    @Autowired
    private FileRepository fileRepository;

    ModelMapper mapperUpdate = new ModelMapper();

    @PostConstruct
    private void init() {
        mapperUpdate.getConfiguration()
                .setSkipNullEnabled(true)
                .setFieldMatchingEnabled(true)
                .setMatchingStrategy(MatchingStrategies.STRICT);

        // Conversor seguro con manejo de errores
        Converter<String, LocalDateTime> localDateTimeConverter = context -> {
            try {
                String source = context.getSource();
                return source != null ? DateTimeUtils.parseToLocalDateTime(source) : null;
            } catch (DateTimeParseException e) {
                throw new IllegalArgumentException("Formato de fecha inválido", e);
            }
        };

        // Registro global del conversor
        mapperUpdate.addConverter(localDateTimeConverter);

        // Mapeo específico para Sales
        mapperUpdate.typeMap(SalesRequest.class, SalesEntity.class)
                .addMappings(m -> m.using(localDateTimeConverter)
                        .map(SalesRequest::getTransactionDateTime, SalesEntity::setTransactionDateTime));
    }

    @Override
    public List<ProductResponse> find(String companyId, Map<String, String> params) {
        Map<String, String> paramsNew = new HashMap<>(params);
        paramsNew.put(Filters.KEY_COMPANY_ID, companyId);
        Specification<ProductEntity> specification = filterWithParameters(paramsNew);
        return productRepository.findAll(specification)
                .stream()
                .map(this::mapperDto)
                .toList();
    }

    @Override
    public ProductResponse findOne(String companyId, String id) {
        Map<String, String> params = Map.of(Filters.KEY_ID, id, Filters.KEY_COMPANY_ID, companyId);
        ProductEntity product = productRepository.findOne(filterWithParameters(params))
                .orElseThrow(() -> new RestExceptionHandler(ApiCodes.API_CODE_404, HttpStatus.NOT_FOUND,
                        String.format(Messages.NOT_FOUND, id)));
        return mapperDto(product);
    }

    @Override
    public ProductResponse save(String companyId, ProductRequest request) {
        CompanyEntity company = companyRepository.findById(UUID.fromString(companyId))
                .orElseThrow(() -> new RestExceptionHandler(ApiCodes.API_CODE_404, HttpStatus.NOT_FOUND,
                        String.format(Messages.NOT_FOUND, companyId)));
        ProductEntity product = mapper.map(request, ProductEntity.class);
        // ligar la categoria con el producto
        CategoryEntity categoria = categoryRepository.findById(UUID.fromString(request.getCategoryId()))
                .orElseThrow(() -> new RestExceptionHandler(ApiCodes.API_CODE_404, HttpStatus.NOT_FOUND,
                        String.format(Messages.NOT_FOUND, request.getCategoryId())));
        product.setCategory(categoria);

        // ligar la unidad de medida con el producto
        UnitMeasurement um = unitMeasurementRepo.findById(UUID.fromString(request.getUnitId()))
                .orElseThrow(() -> new RestExceptionHandler(ApiCodes.API_CODE_404, HttpStatus.NOT_FOUND,
                        String.format(Messages.NOT_FOUND, request.getUnitId())));
        product.setUnit(um);

        if (request.getImageId() != null) {
            FileEntity imageFile = fileRepository.findById(UUID.fromString(request.getImageId()))
                    .orElseThrow(() -> new RestExceptionHandler(ApiCodes.API_CODE_404, HttpStatus.NOT_FOUND,
                            Messages.NOT_FOUND_FILE));
            product.setImage(imageFile);
        }

        try {
            product.setCompany(company);
            product = productRepository.save(product);
        } catch (DataIntegrityViolationException ex) {
            log.error(Messages.LOG_ERROR_DATA_INTEGRITY, ex.getMessage(), ex);
            throw new RestExceptionHandler(ApiCodes.API_CODE_400, HttpStatus.BAD_REQUEST,
                    Messages.DATA_INTEGRITY);
        } catch (Exception e) {
            log.error(Messages.LOG_ERROR_TO_SAVE_ENTITY, e.getMessage(), e);
            throw new RestExceptionHandler(ApiCodes.API_CODE_500, HttpStatus.INTERNAL_SERVER_ERROR,
                    Messages.ERROR_ENTITY_SAVE);
        }
        return mapperDto(product);
    }

    @Override
    public ProductResponse update(String companyId, String id, ProductRequest request) {
        // Verificar que la compañía existe
        CompanyEntity company = companyRepository.findById(UUID.fromString(companyId))
                .orElseThrow(() -> new RestExceptionHandler(ApiCodes.API_CODE_404, HttpStatus.NOT_FOUND,
                        String.format(Messages.NOT_FOUND, companyId)));

        // Buscar el producto existente y verificar que pertenece a la compañía
        Map<String, String> params = Map.of(Filters.KEY_ID, id, Filters.KEY_COMPANY_ID, companyId);
        ProductEntity existingProduct = productRepository.findOne(filterWithParameters(params))
                .orElseThrow(() -> new RestExceptionHandler(ApiCodes.API_CODE_404, HttpStatus.NOT_FOUND,
                        String.format(Messages.NOT_FOUND, id)));

        try {
            // Actualizar los campos básicos del producto
            mapperUpdate.map(request, existingProduct);

            // Actualizar la categoría si viene en el request
            if (request.getCategoryId() != null) {
                CategoryEntity category = categoryRepository.findById(UUID.fromString(request.getCategoryId()))
                        .orElseThrow(() -> new RestExceptionHandler(ApiCodes.API_CODE_404, HttpStatus.NOT_FOUND,
                                String.format(Messages.NOT_FOUND, request.getCategoryId())));
                existingProduct.setCategory(category);
            }

            // Actualizar la unidad de medida si viene en el request
            if (request.getUnitId() != null) {
                UnitMeasurement um = unitMeasurementRepo.findById(UUID.fromString(request.getUnitId()))
                        .orElseThrow(() -> new RestExceptionHandler(ApiCodes.API_CODE_404, HttpStatus.NOT_FOUND,
                                String.format(Messages.NOT_FOUND, request.getUnitId())));
                existingProduct.setUnit(um);
            }

            // Actualizar la imagen si viene en el request
            if (request.getImageId() != null) {
                FileEntity imageFile = fileRepository.findById(UUID.fromString(request.getImageId()))
                        .orElseThrow(() -> new RestExceptionHandler(ApiCodes.API_CODE_404, HttpStatus.NOT_FOUND,
                                Messages.NOT_FOUND_FILE));
                existingProduct.setImage(imageFile);
            }

            // Mantener la relación con la compañía
            existingProduct.setCompany(company);

            // Guardar los cambios
            ProductEntity updatedProduct = productRepository.save(existingProduct);
            return mapperDto(updatedProduct);

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
        ProductEntity existingProduct = productRepository.findOne(filterWithParameters(params))
                .orElseThrow(() -> new RestExceptionHandler(ApiCodes.API_CODE_404, HttpStatus.NOT_FOUND,
                        String.format(Messages.NOT_FOUND, id)));
        try {
            existingProduct.setErased(true);
            productRepository.save(existingProduct);
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

    private ProductResponse mapperDto(ProductEntity source) {
        return mapper.map(source, ProductResponse.class);
    }

    public Specification<ProductEntity> filterWithParameters(Map<String, String> parameters) {
        return new ProductSpecification().getSpecificationByFilters(parameters);
    }
}
