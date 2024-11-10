package com.tien.product.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.tien.product.entity.Product;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;

@Configuration
public class RedisConfig {

      @Value("${spring.redis.host}")
      private String redisHost;

      @Value("${spring.redis.port}")
      private int redisPort;

      @Value("${spring.redis.password}")
      private String redisPassword;

      @Bean
      public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
            RedisTemplate<String, Object> template = new RedisTemplate<>();
            template.setConnectionFactory(redisConnectionFactory);

            Jackson2JsonRedisSerializer<Product> productSerializer = new Jackson2JsonRedisSerializer<>(Product.class);

            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
            objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

            productSerializer.setObjectMapper(objectMapper);

            template.setKeySerializer(new StringRedisSerializer());
            template.setValueSerializer(productSerializer);

            return template;
      }

      @Bean
      public RedisConnectionFactory redisConnectionFactory() {
            RedisStandaloneConfiguration configuration = new RedisStandaloneConfiguration(redisHost, redisPort);
            configuration.setPassword(RedisPassword.of(redisPassword));
            return new LettuceConnectionFactory(configuration);
      }

}