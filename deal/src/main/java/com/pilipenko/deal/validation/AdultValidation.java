package com.pilipenko.deal.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDate;
import java.time.Period;

public class AdultValidation implements ConstraintValidator<Adult, LocalDate> {
    @Override
    public boolean isValid(LocalDate localDate, ConstraintValidatorContext constraintValidatorContext) {
        return Period.between(localDate, LocalDate.now()).getYears() > 18;
    }

    @Override
    public void initialize(Adult constraintAnnotation) {

    }
}
