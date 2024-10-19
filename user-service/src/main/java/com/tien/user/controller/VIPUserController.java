package com.tien.user.controller;

import com.tien.user.dto.ApiResponse;
import com.tien.user.dto.request.VIPUserRequest;
import com.tien.user.dto.response.VIPUserResponse;
import com.tien.user.service.VIPUserService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/vip-user")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class VIPUserController {

      VIPUserService vipUserService;

      @PostMapping("/create")
      public ApiResponse<VIPUserResponse> createVIPUser(@RequestBody @Valid VIPUserRequest request) {
            VIPUserResponse response = vipUserService.createVIPUser(request);
            return ApiResponse.<VIPUserResponse>builder()
                    .result(response)
                    .build();
      }

      @DeleteMapping("/cancel/{username}")
      public ApiResponse<VIPUserResponse> cancelVIPUserSubscription(@PathVariable String username) {
            VIPUserResponse response = vipUserService.cancelVIPUserSubscription(username);
            return ApiResponse.<VIPUserResponse>builder()
                    .result(response)
                    .build();
      }

}