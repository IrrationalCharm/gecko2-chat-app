package eu.irrationalcharm.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Target({
        ElementType.FIELD,
        ElementType.PARAMETER,
        ElementType.RECORD_COMPONENT})
@Constraint(validatedBy = UsernameValidator.class)
@Retention(RetentionPolicy.RUNTIME)
public @interface UsernameValid {

    String message() default "Not a valid username";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
