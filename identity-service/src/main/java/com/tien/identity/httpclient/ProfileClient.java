package com.tien.identity.httpclient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import com.tien.identity.configuration.AuthenticationRequestInterceptor;
import com.tien.identity.dto.request.ProfileCreationRequest;

@FeignClient(name = "profile-service", url = "${app.services.profile}",
        configuration = {AuthenticationRequestInterceptor.class})
public interface ProfileClient {

    // Call the profile creation method in the Profile Service after the user account is created.
    @PostMapping(value = "/internal/users", produces = MediaType.APPLICATION_JSON_VALUE)
    void createProfile(@RequestBody ProfileCreationRequest request);

    // Call the profile deletion method in the Profile Service after the user account is deleted.
    @DeleteMapping("/internal/users/{userId}")
    void deleteProfile(@PathVariable("userId") String userId);

}