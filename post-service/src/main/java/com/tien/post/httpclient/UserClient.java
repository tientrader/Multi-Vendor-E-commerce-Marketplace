package com.tien.post.httpclient;

import com.tien.post.configuration.AuthenticationRequestInterceptor;
import com.tien.post.dto.ApiResponse;
import com.tien.post.dto.response.UserResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "user-service", url = "${app.services.user}",
        configuration = {AuthenticationRequestInterceptor.class})
public interface UserClient {

      @GetMapping(value = "/{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
      ApiResponse<UserResponse> getUserByUserId(@PathVariable String userId);

}