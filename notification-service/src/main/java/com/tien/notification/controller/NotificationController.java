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

      @KafkaListener(topics = "register-successful")
      public void listenUserService(NotificationEvent message) {
            emailService.sendEmail(SendEmailRequest.builder()
                            .to(Recipient.builder()
                                    .email(message.getRecipient())
                                    .build())
                            .subject(message.getSubject())
                            .htmlContent(message.getBody())
                            .build());
      }

      @KafkaListener(topics = "order-created-successful")
      public void listenOrderService(NotificationEvent message) {
            emailService.sendEmail(SendEmailRequest.builder()
                    .to(Recipient.builder()
                            .email(message.getRecipient())
                            .build())
                    .subject(message.getSubject())
                    .htmlContent(message.getBody())
                    .build());
      }

      @KafkaListener(topics = "shop-created-successful")
      public void listenShopService(NotificationEvent message) {
            emailService.sendEmail(SendEmailRequest.builder()
                    .to(Recipient.builder()
                            .email(message.getRecipient())
                            .build())
                    .subject(message.getSubject())
                    .htmlContent(message.getBody())
                    .build());
      }

}