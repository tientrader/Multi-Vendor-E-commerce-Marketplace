package com.tien.file.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

@Configuration
public class S3Config {

      @Value("${cloud.aws.credentials.access-key}")
      String accessKey;

      @Value("${cloud.aws.credentials.secret-key}")
      String secretKey;

      @Value("${cloud.aws.region.static}")
      String region;

      @Bean
      public S3Client s3Client() {
            AwsBasicCredentials awsBasicCredentials = AwsBasicCredentials.create(accessKey, secretKey);

            return S3Client.builder()
                    .region(Region.of(region))
                    .credentialsProvider(StaticCredentialsProvider.create(awsBasicCredentials))
                    .build();
      }

}