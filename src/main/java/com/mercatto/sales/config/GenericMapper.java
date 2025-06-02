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

import jakarta.annotation.PostConstruct;

@Component
public class GenericMapper {
     private final ModelMapper mapper;

    public GenericMapper(){
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
                .setMatchingStrategy(MatchingStrategies.STRICT);
    }

    // Método para registrar convertidores personalizados
    private void registerConverters() {
        Converter<String, LocalDate> localDateConverter = createConverter(DateTimeUtils::parseToLocalDate);
        mapper.addConverter(localDateConverter);
    }

    // Método para registrar mapeos específicos
    private void registerMappings() {
        Converter<String, LocalDate> localDateConverter = createConverter(DateTimeUtils::parseToLocalDate);

        /*mapper.createTypeMap(PatientsRequest.class, PatientsEntity.class)
                .addMappings(m -> m.using(localDateConverter)
                        .map(PatientsRequest::getBirthayDate, PatientsEntity::setBirthayDate));*/
    }

    

    // Método para mapear un objeto
    public <T, R> R map(T source, Class<R> targetClass) {
        try {
            return mapper.map(source, targetClass);
        } catch (Exception e) {
            throw new MappingException("Error al mapear " + source.getClass().getSimpleName() + " a " + targetClass.getSimpleName(), e);
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
