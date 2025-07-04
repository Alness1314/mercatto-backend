package com.mercatto.sales.categories.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.mercatto.sales.categories.dto.request.CategoryRequest;
import com.mercatto.sales.categories.dto.response.CategoryResponse;
import com.mercatto.sales.categories.entity.CategoryEntity;
import com.mercatto.sales.categories.repository.CategoryRepository;
import com.mercatto.sales.categories.service.CategoryService;
import com.mercatto.sales.categories.specification.CategorySpecification;
import com.mercatto.sales.common.api.ApiCodes;
import com.mercatto.sales.common.keys.Filters;
import com.mercatto.sales.common.messages.Messages;
import com.mercatto.sales.common.model.ResponseServerDto;
import com.mercatto.sales.company.entity.CompanyEntity;
import com.mercatto.sales.company.repository.CompanyRepository;
import com.mercatto.sales.config.GenericMapper;
import com.mercatto.sales.exceptions.RestExceptionHandler;
import com.mercatto.sales.utils.UUIDHandler;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class CategoryServiceImpl implements CategoryService {
    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private GenericMapper mapper;

    @Override
    public List<CategoryResponse> find(String companyId, Map<String, String> params) {
        Map<String, String> paramsNew = new HashMap<>(params);
        paramsNew.put(Filters.KEY_COMPANY_ID, companyId);
        Specification<CategoryEntity> specification = filterWithParameters(paramsNew);
        return categoryRepository.findAll(specification)
                .stream()
                .map(this::mapperDto)
                .toList();
    }

    @Override
    public CategoryResponse findOne(String companyId, String id) {
        Map<String, String> params = Map.of(Filters.KEY_ID, id, Filters.KEY_COMPANY_ID, companyId);
        CategoryEntity category = categoryRepository.findOne(filterWithParameters(params))
                .orElseThrow(() -> new RestExceptionHandler(ApiCodes.API_CODE_404, HttpStatus.NOT_FOUND,
                        String.format(Messages.NOT_FOUND, id)));
        return mapperDto(category);
    }

    @Override
    public CategoryResponse save(String companyId, CategoryRequest request) {
        CompanyEntity company = companyRepository.findById(UUIDHandler.toUUID(companyId))
                .orElseThrow(() -> new RestExceptionHandler(ApiCodes.API_CODE_404, HttpStatus.NOT_FOUND,
                        String.format(Messages.NOT_FOUND, companyId)));
        CategoryEntity category = mapper.map(request, CategoryEntity.class);
        try {
            category.setCompany(company);
            category = categoryRepository.save(category);
        } catch (DataIntegrityViolationException ex) {
            log.error(Messages.LOG_ERROR_DATA_INTEGRITY, ex.getMessage(), ex);
            throw new RestExceptionHandler(ApiCodes.API_CODE_400, HttpStatus.BAD_REQUEST,
                    Messages.DATA_INTEGRITY);
        } catch (Exception e) {
            log.error(Messages.LOG_ERROR_TO_SAVE_ENTITY, e.getMessage(), e);
            throw new RestExceptionHandler(ApiCodes.API_CODE_500, HttpStatus.INTERNAL_SERVER_ERROR,
                    Messages.ERROR_ENTITY_SAVE);
        }
        return mapperDto(category);
    }

    @Override
    public CategoryResponse update(String companyId, String id, CategoryRequest request) {
        // Verificar que la compañía existe
        CompanyEntity company = companyRepository.findById(UUIDHandler.toUUID(companyId))
                .orElseThrow(() -> new RestExceptionHandler(ApiCodes.API_CODE_404, HttpStatus.NOT_FOUND,
                        String.format(Messages.NOT_FOUND, companyId)));

        // Buscar la categoría existente que pertenezca a esta compañía
        Map<String, String> params = Map.of(Filters.KEY_ID, id, Filters.KEY_COMPANY_ID, companyId);
        CategoryEntity existingCategory = categoryRepository.findOne(filterWithParameters(params))
                .orElseThrow(() -> new RestExceptionHandler(ApiCodes.API_CODE_404, HttpStatus.NOT_FOUND,
                        String.format(Messages.NOT_FOUND, id)));

        try {
            // Actualizar los campos de la categoría con los nuevos valores
            mapper.map(request, existingCategory);
            existingCategory.setCompany(company); // Asegurar que mantiene la misma compañía

            // Guardar los cambios
            CategoryEntity updatedCategory = categoryRepository.save(existingCategory);
            return mapperDto(updatedCategory);
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
        // Buscar la categoría existente que pertenezca a esta compañía
        Map<String, String> params = Map.of(Filters.KEY_ID, id, Filters.KEY_COMPANY_ID, companyId);
        CategoryEntity existingCategory = categoryRepository.findOne(filterWithParameters(params))
                .orElseThrow(() -> new RestExceptionHandler(ApiCodes.API_CODE_404, HttpStatus.NOT_FOUND,
                        String.format(Messages.NOT_FOUND, id)));
        try {
            existingCategory.setErased(true);
            categoryRepository.save(existingCategory);
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

    private CategoryResponse mapperDto(CategoryEntity source) {
        return mapper.map(source, CategoryResponse.class);
    }

    public Specification<CategoryEntity> filterWithParameters(Map<String, String> parameters) {
        return new CategorySpecification().getSpecificationByFilters(parameters);
    }

}
