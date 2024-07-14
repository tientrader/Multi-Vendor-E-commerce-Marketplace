package com.tien.identity.httpclient;

import com.tien.identity.configuration.AuthenticationRequestInterceptor;
import com.tien.identity.dto.request.ExchangeTokenRequest;
import com.tien.identity.dto.response.ExchangeTokenResponse;
import feign.QueryMap;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(name = "outbound-identity", url = "https://oauth2.googleapis.com",
        configuration = {AuthenticationRequestInterceptor.class})
public interface OutboundIdentityClient {

      // Exchange authorization code for access token
      @PostMapping(value = "/token", produces = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
      ExchangeTokenResponse exchangeToken(@QueryMap ExchangeTokenRequest request);

}