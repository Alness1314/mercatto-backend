package com.mercatto.sales.annotations.build;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.lang.annotation.ElementType;
import java.lang.annotation.RetentionPolicy;

import com.mercatto.sales.annotations.core.RangeValidator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = RangeValidator.class)
public @interface RangeBigDecimal {
    String message() default "El valor está fuera del rango permitido.";

    String min();

    String max();

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
