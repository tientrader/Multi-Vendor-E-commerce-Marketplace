package com.tien.user.service;

import com.tien.user.dto.request.VIPUserRequest;
import com.tien.user.dto.response.VIPUserResponse;

public interface VIPUserService {

      VIPUserResponse createVIPUser(VIPUserRequest request);

      VIPUserResponse createVIPUserWithSession(VIPUserRequest request);

      void updateVipEndDate(String username, String packageType);

      void cancelVIPUserSubscription();

      VIPUserResponse checkIfUserIsVIP(String username);

}