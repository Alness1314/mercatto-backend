package com.mercatto.sales.annotations.core;

import java.math.BigDecimal;

import com.mercatto.sales.annotations.build.RangeBigDecimal;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class RangeValidator implements ConstraintValidator<RangeBigDecimal, BigDecimal> {
    private BigDecimal min;
    private BigDecimal max;

    @Override
    public void initialize(RangeBigDecimal constraintAnnotation) {
        this.min = new BigDecimal(constraintAnnotation.min());
        this.max = new BigDecimal(constraintAnnotation.max());
    }

    @Override
    public boolean isValid(BigDecimal value, ConstraintValidatorContext context) {
        return value != null && value.compareTo(min) >= 0 && value.compareTo(max) <= 0;
    }
}
