package com.mercatto.sales.annotations.core;

import java.util.Arrays;
import java.lang.reflect.Field;

import com.mercatto.sales.annotations.build.ValidEnum;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class EnumValidator implements ConstraintValidator<ValidEnum, String>{
    private Class<? extends Enum<?>> enumClass;
    private String fieldName;

    @Override
    public void initialize(ValidEnum constraintAnnotation) {
        this.enumClass = constraintAnnotation.enumClass();
        this.fieldName = constraintAnnotation.field();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }

        if (fieldName.isEmpty()) {
            // Validación para un enum simple
            return Arrays.stream(enumClass.getEnumConstants())
                    .anyMatch(e -> e.name().equals(value));
        } else {
            // Validación para un enum compuesto (con un campo específico)
            return Arrays.stream(enumClass.getEnumConstants())
                    .anyMatch(e -> {
                        try {
                            Field field = enumClass.getDeclaredField(fieldName);
                            field.setAccessible(true);
                            return value.equals(field.get(e).toString());
                        } catch (NoSuchFieldException | IllegalAccessException ex) {
                            return false;
                        }
                    });
        }
    }

}
