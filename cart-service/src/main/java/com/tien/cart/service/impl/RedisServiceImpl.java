package com.tien.cart.service.impl;

import com.tien.cart.service.RedisService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RedisServiceImpl implements RedisService {

      RedisTemplate<String, Object> redisTemplate;

      @Value("${spring.redis.default.ttl}")
      @NonFinal
      long defaultTTL;

      public void saveWithDefaultTTL(String key, Object value) {
            redisTemplate.opsForValue().set(key, value);
            redisTemplate.expire(key, defaultTTL, TimeUnit.DAYS);
      }

}