package com.mercatto.sales.annotations.core;

import java.util.regex.Pattern;

import com.mercatto.sales.annotations.build.IsTimeString;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TimeValidator implements ConstraintValidator<IsTimeString, String> {
    private static final Pattern TIME_PATTERN_1 = Pattern.compile(buildRegexTime());
    private static final Pattern TIME_PATTERN_2 = Pattern.compile(buildRegexTimeWithTimeZone());

    private static String buildRegexTime() {
        String hour = "^([01][0-9]|2[0-3])";
        String min = "(:[0-5][0-9])";
        log.debug("Pattern 1: {}", String.format("%s:%s{2}$", hour, min));
        return String.format("%s%s{2}$", hour, min);
    }

    private static String buildRegexTimeWithTimeZone() {
        String hour = "^([01][0-9]|2[0-3])";
        String min = "(:[0-5][0-9])";
        String timeZone = "([01][0-9]|2[0-3])(:[0-5][0-9])";
        log.debug("Pattern 1: {}", String.format("%s:%s{2}[-|+]%s$", hour, min, timeZone));
        return String.format("%s%s{2}[-|+]%s$", hour, min, timeZone);
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.trim().isEmpty()) {
            return true;
        }

        boolean matchesPattern1 = TIME_PATTERN_1.matcher(value).matches();
        boolean matchesPattern2 = TIME_PATTERN_2.matcher(value).matches();

        log.debug("Validando '{}', resultado: Pattern1={} Pattern2={}", value, matchesPattern1, matchesPattern2);

        return matchesPattern1 || matchesPattern2;
    }
}
