package com.tien.file.service.impl;

import com.tien.file.dto.response.FileResponse;
import com.tien.file.entity.File;
import com.tien.file.exception.AppException;
import com.tien.file.exception.ErrorCode;
import com.tien.file.mapper.FileMapper;
import com.tien.file.repository.FileRepository;
import com.tien.file.service.FileService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.lambda.LambdaClient;
import software.amazon.awssdk.services.lambda.model.InvokeRequest;
import software.amazon.awssdk.services.lambda.model.InvokeResponse;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class FileServiceImpl implements FileService {

      S3Client s3Client;
      FileMapper fileMapper;
      FileRepository fileRepository;
      LambdaClient lambdaClient;

      @Value("${cloud.aws.lambda.function}")
      @NonFinal
      String lambdaFunctionName;

      @Value("${cloud.aws.s3.bucket}")
      @NonFinal
      String bucketName;

      @Value("${cloud.aws.region.static}")
      @NonFinal
      String region;

      @Override
      public FileResponse uploadFile(MultipartFile file) throws IOException {
            String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();

            uploadToS3(file, fileName);

            String fileUrl = generateFileUrl(fileName);
            File fileEntity = saveFileMetadata(file, fileName, fileUrl);

            String resizedFileUrl = invokeLambdaFunction(fileName, fileUrl);

            fileEntity.setUrl(resizedFileUrl);
            fileRepository.save(fileEntity);

            return fileMapper.toFileUploadResponse(fileEntity);
      }

      @Override
      public List<FileResponse> uploadMultipleFiles(List<MultipartFile> files) {
            List<FileResponse> responses = new ArrayList<>();

            files.parallelStream().forEach(file -> {
                  try {
                        String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();

                        uploadToS3(file, fileName);

                        String fileUrl = generateFileUrl(fileName);
                        File fileEntity = saveFileMetadata(file, fileName, fileUrl);

                        String resizedFileUrl = invokeLambdaFunction(fileName, fileUrl);

                        fileEntity.setUrl(resizedFileUrl);
                        fileRepository.save(fileEntity);

                        synchronized (responses) {
                              responses.add(fileMapper.toFileUploadResponse(fileEntity));
                        }
                  } catch (IOException e) {
                        log.error("Failed to upload file: {}", file.getOriginalFilename(), e);
                  }
            });

            return responses;
      }

      @Override
      @PreAuthorize("hasRole('ADMIN')")
      public void deleteFile(String fileName) {
            s3Client.deleteObject(deleteRequest -> deleteRequest.bucket(bucketName).key(fileName));
            fileRepository.deleteByName(fileName);
      }

      @Override
      @PreAuthorize("hasRole('ADMIN')")
      public FileResponse getFile(String fileName) {
            File fileEntity = fileRepository.findByName(fileName)
                    .orElseThrow(() -> new AppException(ErrorCode.FILE_NOT_FOUND));
            return fileMapper.toFileUploadResponse(fileEntity);
      }

      private String generateFileUrl(String fileName) {
            return "https://" + bucketName + ".s3." + region + ".amazonaws.com/" + fileName;
      }

      private void uploadToS3(MultipartFile file, String fileName) throws IOException {
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(fileName)
                    .contentType(file.getContentType())
                    .build();

            s3Client.putObject(putObjectRequest, RequestBody.fromBytes(file.getBytes()));
      }

      private String invokeLambdaFunction(String fileName, String fileUrl) {
            try {
                  InvokeRequest invokeRequest = InvokeRequest.builder()
                          .functionName(lambdaFunctionName)
                          .payload(SdkBytes.fromUtf8String("{\"fileName\": \"" + fileName + "\", \"fileUrl\": \"" + fileUrl + "\"}"))
                          .build();

                  InvokeResponse invokeResponse = lambdaClient.invoke(invokeRequest);
                  String resizedFileUrl = invokeResponse.payload().asUtf8String();
                  resizedFileUrl = resizedFileUrl.replace("\"", "");

                  return resizedFileUrl;
            } catch (Exception e) {
                  log.error("Failed to invoke Lambda function: {}", lambdaFunctionName, e);
                  return fileUrl;
            }
      }

      private File saveFileMetadata(MultipartFile file, String fileName, String fileUrl) {
            File fileEntity = new File();
            fileEntity.setName(fileName);
            fileEntity.setUrl(fileUrl);
            fileEntity.setSize(file.getSize());
            fileEntity.setType(file.getContentType());

            fileRepository.save(fileEntity);
            return fileEntity;
      }

}