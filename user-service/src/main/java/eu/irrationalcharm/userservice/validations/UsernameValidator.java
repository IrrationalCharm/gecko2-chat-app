package eu.irrationalcharm.userservice.validations;

import eu.irrationalcharm.userservice.annotation.UsernameValid;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class UsernameValidator implements ConstraintValidator<UsernameValid, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return value.matches("^[a-zA-Z0-9_-]{3,20}$");

    }
}
