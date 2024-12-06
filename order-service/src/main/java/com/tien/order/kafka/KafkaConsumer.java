package com.tien.order.kafka;

import com.tien.event.dto.PaymentResponse;
import com.tien.order.service.OrderService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class KafkaConsumer {

      OrderService orderService;
      KafkaProducer kafkaProducer;

      @KafkaListener(topics = "payment-response")
      public void listenPaymentResponse(PaymentResponse paymentResponse) {
            try {
                  String newStatus = paymentResponse.isSuccess() ? "PAID" : "FAILED";
                  orderService.updateOrderStatus(paymentResponse.getOrderId(), newStatus);
            } catch (Exception e) {
                  log.error("Failed to process payment response: {}", paymentResponse, e);
                  kafkaProducer.send("payment-response-dlq", paymentResponse);
            }
      }

}