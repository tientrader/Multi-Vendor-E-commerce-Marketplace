package com.tien.post.service.impl;

import com.tien.post.exception.AppException;
import com.tien.post.exception.ErrorCode;
import com.tien.post.service.AuthenticationService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationServiceImpl implements AuthenticationService {

      public String getAuthenticatedUserId() {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated()) {
                  throw new AppException(ErrorCode.UNAUTHENTICATED);
            }
            return authentication.getName();
      }

}