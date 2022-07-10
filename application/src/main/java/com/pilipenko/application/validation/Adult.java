package com.pilipenko.application.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = AdultValidation.class)
public @interface Adult {
    String message() default "Age should be greater then 18";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
