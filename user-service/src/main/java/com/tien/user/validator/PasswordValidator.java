package com.tien.user.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PasswordValidator implements ConstraintValidator<PasswordConstraint, String> {

      private static final String PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[A-Z])(?=.*[!@#$%^&*(),.?\":{}|<>]).{8,}$";

      @Override
      public boolean isValid(String password, ConstraintValidatorContext context) {
            return password != null && password.matches(PASSWORD_PATTERN);
      }

}