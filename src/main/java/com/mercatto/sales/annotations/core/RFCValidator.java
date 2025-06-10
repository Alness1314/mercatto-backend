package com.mercatto.sales.annotations.core;

import java.util.Arrays;
import java.util.regex.Pattern;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import org.apache.commons.lang3.StringUtils;

import com.mercatto.sales.annotations.build.IsRFC;

public class RFCValidator implements ConstraintValidator<IsRFC, String> {
    private static final Pattern RFC_PATTERN = Pattern.compile(buildRegex());

    private static String buildRegex() {
        String prefixPart = StringUtils
                .join(Arrays.asList("[A-ZÑ]", "\\&"), "|");
        String numberRange = StringUtils.join(Arrays.asList("0-9"));
        String locationPart1 = StringUtils.join(Arrays.asList("0[1-9]", "1[0-2]"), "|");
        String locationPart2 = StringUtils.join(Arrays.asList("[12][0-9]", "0[1-9]", "3[01]"), "|");

        return String.format("(%s){3,4}%s{2}(%s)(%s)[A-Z0-9]{3}$", prefixPart, numberRange, locationPart1,
                locationPart2);
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {

        if (value == null || value.trim().isEmpty()) {
            return true;
        }

        return RFC_PATTERN.matcher(value).matches();
    }

}
