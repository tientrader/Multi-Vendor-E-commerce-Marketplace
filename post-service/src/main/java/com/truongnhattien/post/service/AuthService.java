package com.truongnhattien.post.service;

import com.truongnhattien.post.exception.AppException;
import com.truongnhattien.post.exception.ErrorCode;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

      public String getAuthenticatedUserId() {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated()) {
                  throw new AppException(ErrorCode.UNAUTHENTICATED);
            }
            return authentication.getName();
      }

}