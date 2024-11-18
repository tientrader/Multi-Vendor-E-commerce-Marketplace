package com.tien.gateway.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/fallback")
public class FallbackController {

      @GetMapping("/user")
      public Mono<String> userFallback() {
            return Mono.just("User service is currently unavailable. Please try again later.");
      }

      @GetMapping("/shop")
      public Mono<String> shopFallback() {
            return Mono.just("Shop service is currently unavailable. Please try again later.");
      }

      @GetMapping("/product")
      public Mono<String> productFallback() {
            return Mono.just("Product service is currently unavailable. Please try again later.");
      }

      @GetMapping("/cart")
      public Mono<String> cartFallback() {
            return Mono.just("Cart service is currently unavailable. Please try again later.");
      }

      @GetMapping("/order")
      public Mono<String> orderFallback() {
            return Mono.just("Order service is currently unavailable. Please try again later.");
      }

      @GetMapping("/post")
      public Mono<String> postFallback() {
            return Mono.just("Post service is currently unavailable. Please try again later.");
      }

      @GetMapping("/notification")
      public Mono<String> notificationFallback() {
            return Mono.just("Notification service is currently unavailable. Please try again later.");
      }

      @GetMapping("/payment")
      public Mono<String> paymentFallback() {
            return Mono.just("Payment service is currently unavailable. Please try again later.");
      }

      @GetMapping("/file")
      public Mono<String> fileFallback() {
            return Mono.just("File service is currently unavailable. Please try again later.");
      }

      @GetMapping("/review")
      public Mono<String> reviewFallback() {
            return Mono.just("Review service is currently unavailable. Please try again later.");
      }

}