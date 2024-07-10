package com.tien.notification.controller;

import com.tien.notification.dto.ApiResponse;
import com.tien.notification.dto.request.SendEmailRequest;
import com.tien.notification.dto.response.EmailResponse;
import com.tien.notification.service.EmailService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class EmailController {

    static Logger log = LoggerFactory.getLogger(EmailController.class);

    EmailService emailService;

    @PostMapping("/email/send")
    ApiResponse<EmailResponse> sendEmail(@RequestBody SendEmailRequest request){
        return ApiResponse.<EmailResponse>builder()
                .result(emailService.sendEmail(request))
                .build();
    }

    // Lắng nghe các message Kafka từ topic "onboard-successful"
    @KafkaListener(topics = "onboard-successful")
    public void listenFromIdentityService(String message) {
        log.info("Message received: {}", message);
    }

    // Lắng nghe các message Kafka từ topic "onboard-successful"
    @KafkaListener(topics = "order-successful")
    public void listenFromOrderService(String message) {
        log.info("Message received: {}", message);
    }

}