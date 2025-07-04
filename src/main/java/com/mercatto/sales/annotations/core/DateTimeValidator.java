package com.mercatto.sales.annotations.core;

import java.util.regex.Pattern;

import com.mercatto.sales.annotations.build.IsDateTimeString;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DateTimeValidator implements ConstraintValidator<IsDateTimeString, String> {
    private static final Pattern DATETIME_PATTERN = Pattern.compile(buildRegexDateTime());
    private static final Pattern DATETIME_OFFSET_PATTERN = Pattern.compile(buildRegexDateTimeWithTimeZone());

    private static String buildRegexDateTime() {
        String partOne = "^(?:20\\d{2}|21[0-9]{2})";
        String partTwo = "(0[1-9]|1[0-2])-(0[1-9]|[12][0-9]|3[01])";
        String partThree = "(?:[01][0-9]|2[0-3]):[0-5][0-9]:[0-5][0-9]$";

        log.info("pattern datetime: {}", String.format("%s-%s %s", partOne, partTwo, partThree));
        return String.format("%s-%s %s", partOne, partTwo, partThree);
    }

    private static String buildRegexDateTimeWithTimeZone() {
        String partOne = "^\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}(\\.\\d{7})?";
        String partTwo = "[+-]\\d{2}:\\d{2}$";

        log.info("pattern datetime offset: {}", String.format("%s %s$", partOne, partTwo));
        return String.format("%s %s$", partOne, partTwo);
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.trim().isEmpty()) {
            return true;
        }

        boolean matchesPattern1 = DATETIME_PATTERN.matcher(value).matches();
        boolean matchesPattern2 = DATETIME_OFFSET_PATTERN.matcher(value).matches();

        log.info("Validando '{}', resultado: Pattern1={} Pattern2={}", value, matchesPattern1, matchesPattern2);

        return matchesPattern1 || matchesPattern2;
    }
}
