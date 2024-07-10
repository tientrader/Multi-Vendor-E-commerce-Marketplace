package com.tien.order.httpclient;

import com.tien.order.configuration.AuthenticationRequestInterceptor;
import com.tien.order.dto.response.UserProfileResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "profile-service", url = "${app.services.profile}",
        configuration = {AuthenticationRequestInterceptor.class})
public interface ProfileClient {

      @GetMapping(value = "/users/{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
      UserProfileResponse getProfileByUserId(@PathVariable("userId") String userId);

}