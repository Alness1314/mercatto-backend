package com.mercatto.sales.annotations.build;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.mercatto.sales.annotations.core.NumberValidator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = NumberValidator.class)
public @interface IsNumberString {
    String message() default "No es un valor numerico.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
