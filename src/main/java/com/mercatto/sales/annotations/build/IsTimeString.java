package com.mercatto.sales.annotations.build;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.mercatto.sales.annotations.core.TimeValidator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = TimeValidator.class)
public @interface IsTimeString {
    String message() default "El valor no es una hora válida con formato 'HH:mm:ss' o 'HH:mm:ss+-HH:mm'.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
