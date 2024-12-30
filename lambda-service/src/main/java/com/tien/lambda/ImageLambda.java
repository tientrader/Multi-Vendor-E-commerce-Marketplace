package com.tien.lambda;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import net.coobird.thumbnailator.Thumbnails;
import com.amazonaws.services.s3.model.DeleteObjectRequest;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

public class ImageLambda implements RequestHandler<Map<String, String>, String> {

      private final AmazonS3 s3Client = AmazonS3ClientBuilder.defaultClient();
      private static final String BUCKET_NAME = "tienpro";

      @Override
      public String handleRequest(Map<String, String> event, Context context) {
            try {
                  String fileName = event.get("fileName");

                  InputStream s3Object = s3Client.getObject(new GetObjectRequest(BUCKET_NAME, fileName)).getObjectContent();
                  if (s3Object == null) {
                        System.out.println("Failed to get object: " + fileName);
                        return "Error: File not found in S3";
                  }

                  ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                  Thumbnails.of(s3Object)
                          .size(800, 600)
                          .outputQuality(0.5)
                          .toOutputStream(outputStream);

                  String outputKey = "resized/resized_" + fileName;

                  InputStream resizedImageInputStream = new ByteArrayInputStream(outputStream.toByteArray());
                  s3Client.putObject(new PutObjectRequest(BUCKET_NAME, outputKey, resizedImageInputStream, null));

                  if (!fileName.startsWith("resized/")) {
                        s3Client.deleteObject(new DeleteObjectRequest(BUCKET_NAME, fileName));
                  }

                  return generateResizedFileUrl(outputKey);

            } catch (IOException e) {
                  System.err.println("Error processing file: " + e.getMessage());
                  return "Error processing file: " + e.getMessage();
            }
      }

      private String generateResizedFileUrl(String outputKey) {
            return "https://" + BUCKET_NAME + ".s3.amazonaws.com/" + outputKey;
      }

}