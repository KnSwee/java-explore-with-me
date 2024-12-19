package ru.practicum.main.util;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = NotBlankAnnotationConstraintValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface NotBlankAnnotation {
    String message() default "Поле должно быть или null или NotBlank";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
