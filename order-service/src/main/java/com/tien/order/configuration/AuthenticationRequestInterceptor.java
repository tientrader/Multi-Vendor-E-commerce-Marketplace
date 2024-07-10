package com.tien.order.configuration;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

public class AuthenticationRequestInterceptor implements RequestInterceptor {

    // Sử dụng thông tin xác thực từ header "Authorization" của request gốc và thêm vào template của Feign client
    // Để tự động xác thực khi gửi request đến các service khác
    @Override
    public void apply(RequestTemplate template) {
        ServletRequestAttributes servletRequestAttributes =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

          assert servletRequestAttributes != null;
          var authHeader = servletRequestAttributes.getRequest().getHeader("Authorization");

        if (StringUtils.hasText(authHeader)) template.header("Authorization", authHeader);
    }

}