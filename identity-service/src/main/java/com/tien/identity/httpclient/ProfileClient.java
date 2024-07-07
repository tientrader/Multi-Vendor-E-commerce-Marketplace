package com.tien.identity.httpclient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import com.tien.identity.configuration.AuthenticationRequestInterceptor;
import com.tien.identity.dto.request.ProfileCreationRequest;

@FeignClient(name = "profile-service", url = "${app.services.profile}",
        configuration = {AuthenticationRequestInterceptor.class})
public interface ProfileClient {

    // Tự động gọi đến method tạo profile ở Profile Service sau khi User tạo tài khoản
    @PostMapping(value = "/internal/users", produces = MediaType.APPLICATION_JSON_VALUE)
    void createProfile(@RequestBody ProfileCreationRequest request);

    // Tự động gọi đến method xoá profile ở Profile Service sau khi User xoá tài khoản
    @DeleteMapping("/internal/users/{userId}")
    void deleteProfile(@PathVariable("userId") String userId);

}