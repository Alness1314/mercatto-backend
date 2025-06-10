package com.mercatto.sales.config;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Function;

import org.modelmapper.AbstractConverter;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeMap;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.stereotype.Component;

import com.mercatto.sales.utils.DateTimeUtils;
import com.mercatto.sales.utils.TextEncrypterUtil;
import com.mercatto.sales.taxpayer.dto.response.TaxpayerResponse;
import com.mercatto.sales.taxpayer.entity.LegalRepresentativeEntity;
import com.mercatto.sales.taxpayer.entity.TaxpayerEntity;
import com.mercatto.sales.transactions.dto.request.SalesRequest;
import com.mercatto.sales.transactions.entity.SalesEntity;

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
                .setMatchingStrategy(MatchingStrategies.STRICT);
    }

    // Método para registrar convertidores personalizados
    private void registerConverters() {
        Converter<String, LocalDate> localDateConverter = createConverter(DateTimeUtils::parseToLocalDate);
        Converter<String, LocalDateTime> localDateTimeConverter = createConverter(DateTimeUtils::parseToLocalDateTime);
        mapper.addConverter(localDateConverter);
        mapper.addConverter(localDateTimeConverter);
    }

    // Método para registrar mapeos específicos
    private void registerMappings() {
        // Mapeo para desencriptar campos en TaxpayerResponse
        /*TypeMap<TaxpayerEntity, TaxpayerResponse> taxpayerMap = mapper.createTypeMap(TaxpayerEntity.class,
                TaxpayerResponse.class);

        taxpayerMap.addMappings(mpa -> {
            mpa.using(ctx -> {
                TaxpayerEntity source = (TaxpayerEntity) ctx.getSource();
                String value = source.getRfc();
                String key = source.getDataKey();
                return key != null ? TextEncrypterUtil.decrypt(value, key) : value;
            }).map(src -> src, TaxpayerResponse::setRfc);

            mpa.using(ctx -> {
                TaxpayerEntity source = (TaxpayerEntity) ctx.getSource();
                String value = source.getCorporateReasonOrNaturalPerson();
                String key = source.getDataKey();
                return key != null ? TextEncrypterUtil.decrypt(value, key) : value;
            }).map(src -> src, TaxpayerResponse::setCorporateReasonOrNaturalPerson);

            mpa.using(ctx -> {
                LegalRepresentativeEntity source = (LegalRepresentativeEntity) ctx.getSource();
                String value = source.getRfc();
                String key = source.getDataKey();
                return key != null ? TextEncrypterUtil.decrypt(value, key) : value;
            }).map(TaxpayerEntity::getLegalRepresentative,
                    (dest, value) -> dest.getLegalRepresentative().setRfc((String) value));

            mpa.using(ctx -> {
                LegalRepresentativeEntity source = (LegalRepresentativeEntity) ctx.getSource();
                String value = source.getFullName();
                String key = source.getDataKey();
                return key != null ? TextEncrypterUtil.decrypt(value, key) : value;
            }).map(TaxpayerEntity::getLegalRepresentative,
                    (dest, value) -> dest.getLegalRepresentative().setFullName((String) value));
        });*/

        // Mapeo de fecha en SalesEntity
        Converter<String, LocalDateTime> localDateTimeConverter = createConverter(DateTimeUtils::parseToLocalDateTime);
        mapper.createTypeMap(SalesRequest.class, SalesEntity.class)
                .addMappings(m -> m.using(localDateTimeConverter)
                        .map(SalesRequest::getTransactionDateTime, SalesEntity::setTransactionDateTime));
    }

    // Método para mapear un objeto
    public <T, R> R map(T source, Class<R> targetClass) {
        try {
            return mapper.map(source, targetClass);
        } catch (Exception e) {
            e.printStackTrace();
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
