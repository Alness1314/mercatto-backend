package com.mercatto.sales.annotations.build;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.mercatto.sales.annotations.core.UUIDValidator;
import com.mercatto.sales.common.messages.Messages;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = UUIDValidator.class)
public @interface IsUUID {
    String message() default Messages.UUID_ANNOTATION;
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
