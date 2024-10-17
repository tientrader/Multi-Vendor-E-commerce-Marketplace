package com.tien.gateway.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FallbackController {

      @RequestMapping("/fallback/user")
      public ResponseEntity<String> userServiceFallback() {
            return new ResponseEntity<>("User Service is currently unavailable. Please try again later.",
                    HttpStatus.SERVICE_UNAVAILABLE);
      }

      @RequestMapping("/fallback/shop")
      public ResponseEntity<String> shopServiceFallback() {
            return new ResponseEntity<>("Shop Service is currently unavailable. Please try again later.",
                    HttpStatus.SERVICE_UNAVAILABLE);
      }

      @RequestMapping("/fallback/product")
      public ResponseEntity<String> productServiceFallback() {
            return new ResponseEntity<>("Product Service is currently unavailable. Please try again later.",
                    HttpStatus.SERVICE_UNAVAILABLE);
      }

      @RequestMapping("/fallback/cart")
      public ResponseEntity<String> cartServiceFallback() {
            return new ResponseEntity<>("Cart Service is currently unavailable. Please try again later.",
                    HttpStatus.SERVICE_UNAVAILABLE);
      }

      @RequestMapping("/fallback/order")
      public ResponseEntity<String> orderServiceFallback() {
            return new ResponseEntity<>("Order Service is currently unavailable. Please try again later.",
                    HttpStatus.SERVICE_UNAVAILABLE);
      }

      @RequestMapping("/fallback/post")
      public ResponseEntity<String> postServiceFallback() {
            return new ResponseEntity<>("Post Service is currently unavailable. Please try again later.",
                    HttpStatus.SERVICE_UNAVAILABLE);
      }

      @RequestMapping("/fallback/notification")
      public ResponseEntity<String> notificationServiceFallback() {
            return new ResponseEntity<>("Notification Service is currently unavailable. Please try again later.",
                    HttpStatus.SERVICE_UNAVAILABLE);
      }

      @RequestMapping("/fallback/payment")
      public ResponseEntity<String> paymentServiceFallback() {
            return new ResponseEntity<>("Payment Service is currently unavailable. Please try again later.",
                    HttpStatus.SERVICE_UNAVAILABLE);
      }

}