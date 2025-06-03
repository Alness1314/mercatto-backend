package com.mercatto.sales.config;

import java.time.LocalDate;
import java.util.List;
import java.util.function.Function;

import org.modelmapper.AbstractConverter;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.stereotype.Component;

import com.mercatto.sales.utils.DateTimeUtils;
import com.mercatto.sales.common.model.entity.CommonEntity;
import com.mercatto.sales.users.dto.request.UserRequest;
import com.mercatto.sales.users.dto.response.UserResponse;
import com.mercatto.sales.users.entity.UserEntity;
import com.mercatto.sales.common.model.dto.CommonResponse;

import jakarta.annotation.PostConstruct;

@Component
public class GenericMapper {
    private final ModelMapper mapper;

    public GenericMapper() {
        this.mapper = new ModelMapper();
    }

    @PostConstruct
    public void init() {
        configureModelMapper();
        registerConverters();
        registerMappings();
    }

    // Configuración general del ModelMapper
    private void configureModelMapper() {
        mapper.getConfiguration()
                .setSkipNullEnabled(true)
                .setFieldMatchingEnabled(true)
                .setMatchingStrategy(MatchingStrategies.STANDARD);
    }

    // Método para registrar convertidores personalizados
    private void registerConverters() {
        Converter<String, LocalDate> localDateConverter = createConverter(DateTimeUtils::parseToLocalDate);
        mapper.addConverter(localDateConverter);
    }

    // Método para registrar mapeos específicos
    private void registerMappings() {

        mapper.createTypeMap(CommonEntity.class, CommonResponse.class)
                .addMappings(m -> {
                    m.map(CommonEntity::getId, CommonResponse::setId);
                    m.map(CommonEntity::getCreateAt, CommonResponse::setCreateAt);
                    m.map(CommonEntity::getUpdateAt, CommonResponse::setUpdateAt);
                    m.map(CommonEntity::getErased, CommonResponse::setErased);
                });

        // Inverso: de CommonResponse a CommonEntity
        mapper.createTypeMap(CommonResponse.class, CommonEntity.class)
                .addMappings(m -> {
                    m.map(CommonResponse::getId, CommonEntity::setId);
                    m.map(CommonResponse::getCreateAt, CommonEntity::setCreateAt);
                    m.map(CommonResponse::getUpdateAt, CommonEntity::setUpdateAt);
                    m.map(CommonResponse::getErased, CommonEntity::setErased);
                });

        mapper.createTypeMap(UserResponse.class, UserEntity.class)
                .addMappings(m -> {
                    m.map(UserResponse::getCompanyId, UserEntity::setCompanyId);
                    m.skip(UserEntity::setId); // Evita que documentoId sobrescriba id
                });

        mapper.createTypeMap(UserRequest.class, UserEntity.class)
                .addMappings(m -> {
                    m.map(UserRequest::getCompanyId, UserEntity::setCompanyId);
                    m.skip(UserEntity::setId); // Evita que documentoId sobrescriba id
                });
    }

    // Método para mapear un objeto
    public <T, R> R map(T source, Class<R> targetClass) {
        try {
            return mapper.map(source, targetClass);
        } catch (Exception e) {
            throw new MappingException(
                    "Error al mapear " + source.getClass().getSimpleName() + " a " + targetClass.getSimpleName(), e);
        }
    }

    // Método para mapear una lista de objetos
    public <T, R> List<R> mapList(List<T> sourceList, Class<R> targetClass) {
        return sourceList.stream()
                .map(element -> map(element, targetClass))
                .toList();
    }

    // Método genérico para crear convertidores personalizados
    private <S, D> Converter<S, D> createConverter(Function<S, D> converterFunction) {
        return new AbstractConverter<>() {
            @Override
            protected D convert(S source) {
                return source == null ? null : converterFunction.apply(source);
            }
        };
    }

    // Método para agregar configuraciones personalizadas al ModelMapper
    public void addCustomMapping(CustomMapping customMapping) {
        customMapping.configure(mapper);
    }

    // Excepción personalizada para el mapeo
    public static class MappingException extends RuntimeException {
        public MappingException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    // Interfaz funcional para configuraciones personalizadas
    @FunctionalInterface
    public interface CustomMapping {
        void configure(ModelMapper modelMapper);
    }
}
