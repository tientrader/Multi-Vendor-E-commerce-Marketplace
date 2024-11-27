package com.tien.user.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PhoneNumberValidator implements ConstraintValidator<PhoneNumberConstraint, String> {

      private static final String PHONE_NUMBER_PATTERN = "^\\+?[0-9]{1,3}[0-9]{9,14}$";

      @Override
      public boolean isValid(String phoneNumber, ConstraintValidatorContext context) {
            return phoneNumber != null && phoneNumber.matches(PHONE_NUMBER_PATTERN);
      }

}