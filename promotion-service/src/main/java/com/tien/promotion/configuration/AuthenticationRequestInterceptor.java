package com.tien.promotion.configuration;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

public class AuthenticationRequestInterceptor implements RequestInterceptor {

      @Override
      public void apply(RequestTemplate template) {
            ServletRequestAttributes servletRequestAttributes =
                    (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

            assert servletRequestAttributes != null;
            var authHeader = servletRequestAttributes.getRequest().getHeader("Authorization");

            if (StringUtils.hasText(authHeader)) template.header("Authorization", authHeader);
      }

}