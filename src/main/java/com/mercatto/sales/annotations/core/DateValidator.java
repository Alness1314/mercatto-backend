package com.mercatto.sales.annotations.core;

import java.util.regex.Pattern;

import com.mercatto.sales.annotations.build.IsDateString;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class DateValidator implements ConstraintValidator<IsDateString, String>{
     private static final Pattern DATE_PATTERN = Pattern.compile(
            "^(19|20)\\d\\d-(0[1-9]|1[0-2])-(0[1-9]|[12]\\d|3[01])$");

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.trim().isEmpty()) {
            return true;
        }

        return DATE_PATTERN.matcher(value).matches();
    }
}
