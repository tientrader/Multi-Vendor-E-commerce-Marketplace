package com.tien.user.service;

import com.tien.user.dto.request.VIPUserRequest;
import com.tien.user.dto.request.VIPUserRequestWithSession;
import com.tien.user.dto.response.VIPUserResponse;
import com.tien.user.dto.response.VIPUserResponseWithSession;

public interface VIPUserService {

      VIPUserResponse createVIPUser(VIPUserRequest request);

      VIPUserResponseWithSession createVIPUserWithSession(VIPUserRequestWithSession request);

      void updateVipEndDate(String username, String packageType);

      void cancelVIPUserSubscription();

      VIPUserResponse checkIfUserIsVIP(String username);

}