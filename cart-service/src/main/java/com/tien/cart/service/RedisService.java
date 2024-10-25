package com.tien.cart.service;

public interface RedisService {

      void saveWithDefaultTTL(String key, Object value);

}