package com.pilipenko.deal.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = MiddleNameValidation.class)

public @interface MiddleNameEpsonOrCorrect {
    String message () default "Middle name should be greater than 2 symbols and less than 30 or not defined";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
