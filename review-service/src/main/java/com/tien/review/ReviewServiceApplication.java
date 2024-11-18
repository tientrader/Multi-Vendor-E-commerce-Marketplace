package com.tien.review;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class ReviewServiceApplication {

      public static void main(String[] args) {
            SpringApplication.run(ReviewServiceApplication.class, args);
      }

}