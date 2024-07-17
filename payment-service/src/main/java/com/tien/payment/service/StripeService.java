package com.tien.payment.service;

import com.stripe.exception.StripeException;
import com.stripe.model.*;
import com.stripe.param.*;
import com.tien.payment.entity.Stripe;
import com.tien.payment.repository.StripeRepository;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class StripeService {

      StripeRepository stripeRepository;

      // Create a PaymentIntent with specified amount, currency, and description
      public PaymentIntent createPaymentIntent(int amount, String currency, String description) throws StripeException {
            PaymentIntentCreateParams createParams = new PaymentIntentCreateParams.Builder()
                    .setCurrency(currency)
                    .setAmount((long) amount)
                    .setDescription(description)
                    .build();

            PaymentIntent paymentIntent = PaymentIntent.create(createParams);

            Stripe stripe = Stripe.builder()
                    .chargeId(paymentIntent.getId())
                    .amount(amount)
                    .currency(currency)
                    .description(description)
                    .status(paymentIntent.getStatus())
                    .build();
            stripeRepository.save(stripe);

            return paymentIntent;
      }

      // Charge a customer with the specified customerId and amount
      public Charge charge(String customerId, int amount) throws StripeException {
            Map<String, Object> chargeParams = new HashMap<>();
            chargeParams.put("amount", amount);
            chargeParams.put("currency", "usd");
            chargeParams.put("customer", customerId);

            Charge charge = Charge.create(chargeParams);

            Stripe stripe = Stripe.builder()
                    .chargeId(charge.getId())
                    .customerId(customerId)
                    .amount(amount)
                    .currency("usd")
                    .description(charge.getDescription())
                    .status(charge.getStatus())
                    .build();
            stripeRepository.save(stripe);

            return charge;
      }

      // Refund a charge with the specified chargeId
      public Refund refund(String chargeId) throws StripeException {
            RefundCreateParams createParams = new RefundCreateParams.Builder()
                    .setCharge(chargeId)
                    .build();

            Refund refund = Refund.create(createParams);

            Stripe stripe = stripeRepository.findByChargeId(chargeId);
            if (stripe != null) {
                  stripe.setStatus("refunded");
                  stripeRepository.save(stripe);
            }

            return refund;
      }

      // Create a new customer with the specified email and source
      public Customer createCustomer(String email, String source) throws StripeException {
            CustomerCreateParams createParams = new CustomerCreateParams.Builder()
                    .setEmail(email)
                    .setSource(source)
                    .build();

            return Customer.create(createParams);
      }

}