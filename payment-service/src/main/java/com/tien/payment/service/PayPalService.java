package com.tien.payment.service;

import com.paypal.api.payments.*;
import com.paypal.base.rest.APIContext;
import com.paypal.base.rest.PayPalRESTException;
import com.tien.payment.dto.request.PayPalRequest;
import com.tien.payment.dto.response.PayPalResponse;
import com.tien.payment.entity.PayPal;
import com.tien.payment.mapper.PayPalMapper;
import com.tien.payment.repository.PayPalRepository;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PayPalService {

      APIContext apiContext;
      PayPalRepository paypalRepository;
      PayPalMapper paypalMapper;

      // Create a PayPal payment with specified details
      @Transactional(rollbackFor = Exception.class)
      public Payment createPayment(PayPalRequest request) throws PayPalRESTException {
            Amount amount = new Amount();
            amount.setCurrency(request.getCurrency());
            amount.setTotal(request.getAmount().toString());

            Transaction transaction = new Transaction();
            transaction.setDescription(request.getDescription());
            transaction.setAmount(amount);

            List<Transaction> transactions = new ArrayList<>();
            transactions.add(transaction);

            Payer payer = new Payer();
            payer.setPaymentMethod(request.getPaymentMethod());

            Payment payment = new Payment();
            payment.setIntent("sale");
            payment.setPayer(payer);
            payment.setTransactions(transactions);

            RedirectUrls redirectUrls = new RedirectUrls();
            redirectUrls.setCancelUrl("http://localhost:8085/payment/paypal/cancel");
            redirectUrls.setReturnUrl("http://localhost:8085/payment/paypal/success");
            payment.setRedirectUrls(redirectUrls);

            return payment.create(apiContext);
      }

      // Execute a PayPal payment using the paymentId and payerId
      @Transactional(rollbackFor = Exception.class)
      public Payment executePayment(Long paymentId, String payerId) throws PayPalRESTException {
            Payment payment = new Payment();
            payment.setId(String.valueOf(paymentId));
            PaymentExecution paymentExecution = new com.paypal.api.payments.PaymentExecution();
            paymentExecution.setPayerId(payerId);
            return payment.execute(apiContext, paymentExecution);
      }

      // Save a PayPal request and return the response
      @Transactional(rollbackFor = Exception.class)
      public PayPalResponse saveAndReturnResponse(PayPalRequest request) {
            PayPal paypal = paypalMapper.toPaypal(request);
            PayPal savedPaypal = paypalRepository.save(paypal);
            return paypalMapper.toPaypalResponse(savedPaypal);
      }

}