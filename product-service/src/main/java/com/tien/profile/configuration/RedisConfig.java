package com.tien.profile.configuration;

import com.fasterxml.jackson.databind.type.TypeFactory;
import com.tien.profile.dto.response.ProductResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.util.List;

@Configuration
public class RedisConfig {

      @Value("${spring.redis.host}")
      private String redisHost;

      @Value("${spring.redis.port}")
      private int redisPort;

      @Value("${spring.redis.password}")
      private String redisPassword;

      @Bean
      public LettuceConnectionFactory redisConnectionFactory() {
            RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration();
            redisStandaloneConfiguration.setHostName(redisHost);
            redisStandaloneConfiguration.setPort(redisPort);
            redisStandaloneConfiguration.setPassword(redisPassword);
            return new LettuceConnectionFactory(redisStandaloneConfiguration);
      }

      @Bean
      public RedisTemplate<String, List<ProductResponse>> listRedisTemplate() {
            RedisTemplate<String, List<ProductResponse>> template = new RedisTemplate<>();
            template.setConnectionFactory(redisConnectionFactory());

            template.setKeySerializer(new StringRedisSerializer());

            Jackson2JsonRedisSerializer<List<ProductResponse>> serializer = new Jackson2JsonRedisSerializer<>(
                    TypeFactory.defaultInstance().constructCollectionType(List.class, ProductResponse.class)
            );
            template.setValueSerializer(serializer);

            return template;
      }

      @Bean
      public RedisTemplate<String, ProductResponse> redisTemplate() {
            RedisTemplate<String, ProductResponse> template = new RedisTemplate<>();
            template.setConnectionFactory(redisConnectionFactory());
            template.setKeySerializer(new StringRedisSerializer());
            template.setValueSerializer(new Jackson2JsonRedisSerializer<>(ProductResponse.class));
            return template;
      }

}