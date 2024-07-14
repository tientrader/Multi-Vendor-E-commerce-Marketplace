package com.tien.identity.httpclient;

import com.tien.identity.dto.response.OutboundUserResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "outbound-user-client", url = "https://www.googleapis.com")
public interface OutboundUserClient {

      // Retrieve user information from Google's User Info API.
      @GetMapping(value = "/oauth2/v1/userinfo")
      OutboundUserResponse getUserInfo(@RequestParam("alt") String alt,
                                       @RequestParam("access_token") String accessToken);

}