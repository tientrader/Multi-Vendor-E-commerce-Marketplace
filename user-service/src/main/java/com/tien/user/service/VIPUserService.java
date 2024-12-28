package com.tien.user.service;

import com.tien.user.dto.request.VIPUserRequestWithSession;
import com.tien.user.dto.response.VIPUserResponse;
import com.tien.user.dto.response.VIPUserResponseWithSession;
import com.tien.user.enums.PackageType;

public interface VIPUserService {

      VIPUserResponseWithSession createVIPUserWithSession(VIPUserRequestWithSession request);

      void updateVipEndDate(String username, PackageType packageType, String subscriptionId);

      void cancelVIPUserSubscription();

      VIPUserResponse checkIfUserIsVIP(String username);

}