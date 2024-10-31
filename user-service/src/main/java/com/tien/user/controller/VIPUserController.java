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

      @PostMapping("/createWithSession")
      public ApiResponse<VIPUserResponse> createVIPUserWithSession(@RequestBody @Valid VIPUserRequest request) {
            VIPUserResponse response = vipUserService.createVIPUserWithSession(request);
            return ApiResponse.<VIPUserResponse>builder()
                    .result(response)
                    .build();
      }

      @PutMapping("/update-end-date/{username}")
      public ApiResponse<Void> updateVipEndDate(@PathVariable String username, @RequestParam String packageType) {
            vipUserService.updateVipEndDate(username, packageType);
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