package com.tien.post.httpclient;

import com.tien.post.configuration.AuthenticationRequestInterceptor;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "user-service", path = "/user", configuration = {AuthenticationRequestInterceptor.class})
public interface UserClient {

      @CircuitBreaker(name = "checkIfUserIsVIP")
      @Retry(name = "checkIfUserIsVIP")
      @GetMapping("/vip-user/check/{username}")
      void checkIfUserIsVIP(@PathVariable("username") String username);

}