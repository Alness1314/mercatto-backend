package com.mercatto.sales.annotations.build;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.mercatto.sales.annotations.core.DateValidator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = DateValidator.class)
public @interface IsDateString {
    String message() default "El valor no es una fecha válida con formato 'yyyy-MM-dd'.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
