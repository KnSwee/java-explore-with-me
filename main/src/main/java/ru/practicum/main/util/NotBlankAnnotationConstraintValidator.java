package ru.practicum.main.util;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class NotBlankAnnotationConstraintValidator implements ConstraintValidator<NotBlankAnnotation, String> {
    @Override
    public boolean isValid(String text, ConstraintValidatorContext constraintValidatorContext) {
        if (text == null) {
            return true;
        } else {
            return !text.isBlank();
        }
    }
}
