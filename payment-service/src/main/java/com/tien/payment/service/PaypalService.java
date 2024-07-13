package com.tien.payment.service;

import com.paypal.api.payments.Amount;
import com.paypal.api.payments.Payment;
import com.paypal.api.payments.Payer;
import com.paypal.api.payments.RedirectUrls;
import com.paypal.api.payments.Transaction;
import com.paypal.base.rest.APIContext;
import com.paypal.base.rest.PayPalRESTException;
import com.tien.payment.dto.PaypalRequest;
import com.tien.payment.dto.PaypalResponse;
import com.tien.payment.entity.Paypal;
import com.tien.payment.mapper.PaypalMapper;
import com.tien.payment.repository.PaypalRepository;
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
public class PaypalService {

      APIContext apiContext;
      PaypalRepository paypalRepository;
      PaypalMapper paypalMapper;

      @Transactional(rollbackFor = Exception.class)
      public Payment createPayment(PaypalRequest request) throws PayPalRESTException {
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

      @Transactional(rollbackFor = Exception.class)
      public Payment executePayment(Long paymentId, String payerId) throws PayPalRESTException {
            Payment payment = new Payment();
            payment.setId(String.valueOf(paymentId));
            com.paypal.api.payments.PaymentExecution paymentExecution = new com.paypal.api.payments.PaymentExecution();
            paymentExecution.setPayerId(payerId);
            return payment.execute(apiContext, paymentExecution);
      }

      @Transactional(rollbackFor = Exception.class)
      public PaypalResponse saveAndReturnResponse(PaypalRequest request) {
            Paypal paypal = paypalMapper.toPaypal(request);
            Paypal savedPaypal = paypalRepository.save(paypal);
            return paypalMapper.toPaypalResponse(savedPaypal);
      }

}