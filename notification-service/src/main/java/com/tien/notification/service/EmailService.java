package com.tien.notification.service;

import com.tien.notification.dto.request.SendEmailRequest;
import com.tien.notification.dto.response.EmailResponse;

public interface EmailService {

      EmailResponse sendEmail(SendEmailRequest request);

}