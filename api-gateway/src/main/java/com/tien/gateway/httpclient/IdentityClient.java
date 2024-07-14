package com.tien.gateway.httpclient;

import com.tien.gateway.dto.ApiResponse;
import com.tien.gateway.dto.request.IntrospectRequest;
import com.tien.gateway.dto.response.IntrospectResponse;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.service.annotation.PostExchange;
import reactor.core.publisher.Mono;

public interface IdentityClient {

    // Call introspect method of Identity Service to check if the token is valid
    @PostExchange(url = "/auth/introspect", contentType = MediaType.APPLICATION_JSON_VALUE)
    Mono<ApiResponse<IntrospectResponse>> introspect(@RequestBody IntrospectRequest request);

}