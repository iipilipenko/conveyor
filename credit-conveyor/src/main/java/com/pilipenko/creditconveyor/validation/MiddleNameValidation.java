package com.pilipenko.creditconveyor.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;

public class MiddleNameValidation implements ConstraintValidator<MiddleNameEpsonOrCorrect, String> {
    @Override
    public void initialize(MiddleNameEpsonOrCorrect constraintAnnotation) {

    }

    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        if (s != null) {
            return Pattern.matches("[a-zA-Z]{2,30}", s);
        }
        return false;

    }
}
