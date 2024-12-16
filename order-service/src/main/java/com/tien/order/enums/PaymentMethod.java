package com.tien.order.enums;

import com.tien.order.exception.AppException;
import com.tien.order.exception.ErrorCode;

public enum PaymentMethod {

      CARD,
      COD;

      public static PaymentMethod fromString(String paymentMethod) {
            try {
                  return PaymentMethod.valueOf(paymentMethod.toUpperCase());
            } catch (IllegalArgumentException e) {
                  throw new AppException(ErrorCode.INVALID_PAYMENT_METHOD);
            }
      }

}