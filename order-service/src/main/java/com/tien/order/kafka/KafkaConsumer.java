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

      @KafkaListener(topics = "payment-response", groupId = "order-service")
      public void listenPaymentResponse(PaymentResponse paymentResponse) {
            log.info("Received payment response: {}", paymentResponse);
            String newStatus = paymentResponse.isSuccess() ? "PAID" : "FAILED";
            orderService.updateOrderStatus(paymentResponse.getOrderId(), newStatus);
      }

}