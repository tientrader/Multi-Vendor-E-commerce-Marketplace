package com.tien.payment.service;

import com.paypal.api.payments.*;
import com.paypal.base.rest.APIContext;
import com.paypal.base.rest.PayPalRESTException;
import com.tien.payment.repository.PaypalRepository;
import com.tien.payment.entity.Paypal;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PaypalService {

      APIContext apiContext;
      PaypalRepository paypalRepository;

      @Transactional(rollbackFor = Exception.class)
      public Payment createPayment(
              double total,
              String currency,
              String method,
              String intent,
              String description,
              String cancelUrl,
              String successUrl
      ) throws PayPalRESTException {
            Amount amount = new Amount();
            amount.setCurrency(currency);
            amount.setTotal(String.format(Locale.US, "%.2f", total));

            Transaction transaction = new Transaction();
            transaction.setDescription(description);
            transaction.setAmount(amount);

            List<Transaction> transactions = new ArrayList<>();
            transactions.add(transaction);

            Payer payer = new Payer();
            payer.setPaymentMethod(method);

            Payment payment = new Payment();
            payment.setIntent(intent);
            payment.setPayer(payer);
            payment.setTransactions(transactions);

            RedirectUrls redirectUrls = new RedirectUrls();
            redirectUrls.setCancelUrl(cancelUrl);
            redirectUrls.setReturnUrl(successUrl);
            payment.setRedirectUrls(redirectUrls);

            Payment createdPayment = payment.create(apiContext);

            Paypal paypalTransaction = new Paypal();
            paypalTransaction.setPaymentId(Long.valueOf(createdPayment.getId()));
            paypalTransaction.setAmount(total);
            paypalTransaction.setCurrency(currency);
            paypalTransaction.setPaymentMethod(method);
            paypalTransaction.setDescription(description);
            paypalTransaction.setPaymentState(createdPayment.getState());
            paypalRepository.save(paypalTransaction);

            return createdPayment;
      }

      @Transactional(rollbackFor = Exception.class)
      public Payment executePayment(Long paymentId, String payerId) throws PayPalRESTException {
            Payment payment = new Payment();
            payment.setId(String.valueOf(paymentId));
            PaymentExecution paymentExecution = new PaymentExecution();
            paymentExecution.setPayerId(payerId);
            Payment executedPayment = payment.execute(apiContext, paymentExecution);

            Paypal paypalTransaction = paypalRepository.findById(paymentId)
                    .orElseThrow(() -> new RuntimeException("Transaction not found"));
            paypalTransaction.setPayerId(payerId);
            paypalTransaction.setPaymentState(executedPayment.getState());
            paypalRepository.save(paypalTransaction);

            return executedPayment;
      }

}