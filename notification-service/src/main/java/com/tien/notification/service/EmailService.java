package com.tien.notification.service;

import com.tien.notification.dto.request.EmailRequest;
import com.tien.notification.dto.request.SendEmailRequest;
import com.tien.notification.dto.request.Sender;
import com.tien.notification.dto.response.EmailResponse;
import com.tien.notification.exception.AppException;
import com.tien.notification.exception.ErrorCode;
import com.tien.notification.httpclient.EmailClient;

import feign.FeignException;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class EmailService {

    EmailClient emailClient;

    @Value("${notification.email.brevo-apikey}")
    @NonFinal
    String apiKey;

    @PreAuthorize("hasRole('ADMIN')")
    public EmailResponse sendEmail(SendEmailRequest request) {
        EmailRequest emailRequest = EmailRequest.builder()
                        .sender(Sender.builder()
                        .name("Truong Nhat Tien")
                        .email("truongnhattien.business@gmail.com")
                        .build())
                .to(List.of(request.getTo()))
                .subject(request.getSubject())
                .htmlContent(request.getHtmlContent())
                .build();
        try {
            return emailClient.sendEmail(apiKey, emailRequest);
        } catch (FeignException e){
            throw new AppException(ErrorCode.CANNOT_SEND_EMAIL);
        }
    }

}