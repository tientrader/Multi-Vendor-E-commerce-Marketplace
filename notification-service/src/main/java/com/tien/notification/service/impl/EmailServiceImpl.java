package com.tien.notification.service.impl;

import com.tien.notification.dto.request.EmailRequest;
import com.tien.notification.dto.request.SendEmailRequest;
import com.tien.notification.dto.request.Sender;
import com.tien.notification.dto.response.EmailResponse;
import com.tien.notification.exception.AppException;
import com.tien.notification.exception.ErrorCode;
import com.tien.notification.httpclient.EmailClient;
import com.tien.notification.service.EmailService;
import feign.FeignException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class EmailServiceImpl implements EmailService {

      EmailClient emailClient;

      @Value("${notification.email.brevo-apikey}")
      @NonFinal
      String apiKey;

      public EmailResponse sendEmail(SendEmailRequest request) {
            EmailRequest emailRequest = EmailRequest.builder()
                    .sender(Sender.builder()
                            .name("Truong Nhat Tien")
                            .email("ntien.se03@gmail.com")
                            .build())
                    .to(List.of(request.getTo()))
                    .subject(request.getSubject())
                    .htmlContent(request.getHtmlContent())
                    .build();
            try {
                  return emailClient.sendEmail(apiKey, emailRequest);
            } catch (FeignException e) {
                  log.error("Error sending email to {}: {}", request.getTo(), e.getMessage(), e);
                  throw new AppException(ErrorCode.SERVICE_UNAVAILABLE);
            }
      }

}