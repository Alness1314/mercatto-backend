package com.mercatto.sales.annotations.core;

import java.util.regex.Pattern;

import com.mercatto.sales.annotations.build.IsNumberString;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class NumberValidator implements ConstraintValidator<IsNumberString, String> {
    private static final Pattern NUMBER_STRING_PATTERN = Pattern.compile("^-?\\d+(\\.\\d+)?$");

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.trim().isEmpty()) {
            return true;
        }

        return NUMBER_STRING_PATTERN.matcher(value).matches();
    }
}
