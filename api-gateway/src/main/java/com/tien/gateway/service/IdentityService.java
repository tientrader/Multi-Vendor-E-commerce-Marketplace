package com.tien.gateway.service;

import com.tien.gateway.dto.ApiResponse;
import com.tien.gateway.dto.request.IntrospectRequest;
import com.tien.gateway.dto.response.IntrospectResponse;
import com.tien.gateway.httpclient.IdentityClient;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class IdentityService {

    IdentityClient identityClient;

    // Tạo một yêu cầu introspect token rồi gửi đến IdentityClient
    public Mono<ApiResponse<IntrospectResponse>> introspect(String token){
        return identityClient.introspect(IntrospectRequest.builder()
                .token(token)
                .build());
    }

}