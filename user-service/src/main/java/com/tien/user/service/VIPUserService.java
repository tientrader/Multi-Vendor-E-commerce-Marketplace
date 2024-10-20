package com.tien.user.service;

import com.tien.user.dto.request.VIPUserRequest;
import com.tien.user.dto.response.VIPUserResponse;

public interface VIPUserService {

      VIPUserResponse createVIPUser(VIPUserRequest request);
      VIPUserResponse cancelVIPUserSubscription(String username);
      VIPUserResponse checkIfUserIsVIP(String username);

}