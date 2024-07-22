package com.tien.notification.controller;

import com.tien.event.dto.NotificationEvent;
import com.tien.notification.dto.request.Recipient;
import com.tien.notification.dto.request.SendEmailRequest;
import com.tien.notification.service.EmailService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@Component
@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class NotificationController {

      EmailService emailService;

      // Listen to Kafka messages from topic "notification-delivery"
      @KafkaListener(topics = "notification-delivery")
      public void listenIdentityService(NotificationEvent message) {
            log.info("Message received: {}", message);
            emailService.sendEmail(SendEmailRequest.builder()
                            .to(Recipient.builder()
                                    .email(message.getRecipient())
                                    .build())
                            .subject(message.getSubject())
                            .htmlContent(message.getBody())
                            .build());
      }

      // Listen to Kafka messages from topic "order-successful"
      @KafkaListener(topics = "order-successful")
      public void listenOrderService(NotificationEvent message) {
            log.info("Message received: {}", message);
            emailService.sendEmail(SendEmailRequest.builder()
                    .to(Recipient.builder()
                            .email(message.getRecipient())
                            .build())
                    .subject(message.getSubject())
                    .htmlContent(message.getBody())
                    .build());
      }

}