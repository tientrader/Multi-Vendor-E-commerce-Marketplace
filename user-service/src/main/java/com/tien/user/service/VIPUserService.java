package com.tien.user.service;

import com.tien.user.dto.request.VIPUserRequest;
import com.tien.user.dto.response.VIPUserResponse;

public interface VIPUserService {

      VIPUserResponse createVIPUser(VIPUserRequest request);

      void cancelVIPUserSubscription();

      VIPUserResponse checkIfUserIsVIP(String username);

}