package com.mercatto.sales.annotations.build;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.mercatto.sales.annotations.core.DateTimeValidator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = DateTimeValidator.class)
public @interface IsDateTimeString {
    String message() default "El valor no es una fecha y hora válida con formato 'yyyy-MM-dd HH:mm:ss' o 'yyyy-MM-dd HH:mm:ss.SSSSSSS xxxxx'.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
