package eu.irrationalcharm.messaging_service.validation;


import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Target({ElementType.TYPE})
@Constraint(validatedBy = MessageValidator.class)
@Retention(RetentionPolicy.RUNTIME)
public @interface MessageValid {

    String message() default "Not a valid message";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
