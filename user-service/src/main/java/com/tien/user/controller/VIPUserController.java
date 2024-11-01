package com.tien.user.controller;

import com.tien.user.dto.ApiResponse;
import com.tien.user.dto.request.VIPUserRequest;
import com.tien.user.dto.request.VIPUserRequestWithSession;
import com.tien.user.dto.response.VIPUserResponse;
import com.tien.user.dto.response.VIPUserResponseWithSession;
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
      public ApiResponse<Void> createVIPUser(@RequestBody @Valid VIPUserRequest request) {
            vipUserService.createVIPUser(request);
            return ApiResponse.<Void>builder()
                    .message("VIP user created successfully.")
                    .build();
      }

      @PostMapping("/createWithSession")
      public ApiResponse<VIPUserResponseWithSession> createVIPUserWithSession(@RequestBody @Valid VIPUserRequestWithSession request) {
            VIPUserResponseWithSession response = vipUserService.createVIPUserWithSession(request);
            return ApiResponse.<VIPUserResponseWithSession>builder()
                    .result(response)
                    .build();
      }

      @PutMapping("/update-end-date/{username}")
      public ApiResponse<Void> updateVipEndDate(@PathVariable String username, @RequestParam String packageType, @RequestParam String subscriptionId) {
            vipUserService.updateVipEndDate(username, packageType, subscriptionId);
            return ApiResponse.<Void>builder()
                    .message("VIP end date updated successfully for user: " + username)
                    .build();
      }

      @DeleteMapping("/cancel")
      public ApiResponse<Void> cancelVIPUserSubscription() {
            vipUserService.cancelVIPUserSubscription();
            return ApiResponse.<Void>builder()
                    .message("The VIP subscription has been successfully canceled!")
                    .build();
      }

      @GetMapping("/check/{username}")
      public ApiResponse<VIPUserResponse> checkIfUserIsVIP(@PathVariable String username) {
            return ApiResponse.<VIPUserResponse>builder()
                    .result(vipUserService.checkIfUserIsVIP(username))
                    .build();
      }

}