package com.tien.user.httpclient;

import com.tien.user.dto.identity.*;
import com.tien.user.dto.request.UserUpdateRequest;
import feign.QueryMap;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "identity-client", url = "${idp.url}")
public interface IdentityClient {

    @PostMapping(value = "/realms/tienproapp/protocol/openid-connect/token",
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    TokenExchangeResponse exchangeToken(@QueryMap TokenExchangeParam param);

    @PostMapping(value = "/admin/realms/tienproapp/users",
            consumes = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<?> createUser(@RequestHeader("authorization") String token,
                                 @RequestBody UserCreationParam param);

    @PutMapping(value = "/admin/realms/tienproapp/users/{id}",
            consumes = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<?> updateUser(@RequestHeader("authorization") String token,
                                 @PathVariable("id") String userId,
                                 @RequestBody UserUpdateRequest param);

    @DeleteMapping(value = "/admin/realms/tienproapp/users/{id}")
    void deleteUser(@RequestHeader("authorization") String token,
                    @PathVariable("id") String userId);

    @PutMapping(value = "/admin/realms/tienproapp/users/{id}/reset-password",
            consumes = MediaType.APPLICATION_JSON_VALUE)
    void resetPassword(@RequestHeader("authorization") String token,
                       @PathVariable("id") String userId,
                       @RequestBody Credential credential);

    @PutMapping(value = "/admin/realms/tienproapp/users/{id}/send-verify-email")
    void sendVerificationEmail(@RequestHeader("Authorization") String authHeader, @PathVariable("id") String userId);

}