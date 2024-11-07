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
import software.amazon.awssdk.core.sync.RequestBody;
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

      @Value("${cloud.aws.s3.bucket}")
      @NonFinal
      String bucketName;

      @Value("${cloud.aws.region.static}")
      @NonFinal
      String region;

      @Override
      public FileResponse uploadFile(MultipartFile file) throws IOException {
            String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();

            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(fileName)
                    .contentType(file.getContentType())
                    .build();

            s3Client.putObject(putObjectRequest, RequestBody.fromBytes(file.getBytes()));

            String fileUrl = "https://" + bucketName + ".s3." + region + ".amazonaws.com/" + fileName;

            File fileEntity = new File();
            fileEntity.setName(fileName);
            fileEntity.setUrl(fileUrl);
            fileEntity.setSize(file.getSize());
            fileEntity.setType(file.getContentType());

            fileRepository.save(fileEntity);

            return fileMapper.toFileUploadResponse(fileEntity);
      }

      @Override
      public List<FileResponse> uploadMultipleFiles(List<MultipartFile> files) throws IOException {
            List<FileResponse> responses = new ArrayList<>();

            for (MultipartFile file : files) {
                  String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();

                  PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                          .bucket(bucketName)
                          .key(fileName)
                          .contentType(file.getContentType())
                          .build();

                  s3Client.putObject(putObjectRequest, RequestBody.fromBytes(file.getBytes()));

                  String fileUrl = "https://" + bucketName + ".s3." + region + ".amazonaws.com/" + fileName;

                  File fileEntity = new File();
                  fileEntity.setName(fileName);
                  fileEntity.setUrl(fileUrl);
                  fileEntity.setSize(file.getSize());
                  fileEntity.setType(file.getContentType());

                  fileRepository.save(fileEntity);

                  responses.add(fileMapper.toFileUploadResponse(fileEntity));
            }

            return responses;
      }

      @Override
      @PreAuthorize("hasRole('ADMIN')")
      public void deleteFile(String fileName) {
            s3Client.deleteObject(deleteRequest -> deleteRequest.bucket(bucketName).key(fileName));
            fileRepository.deleteByName(fileName);
      }

      @Override
      public FileResponse getFile(String fileName) {
            File fileEntity = fileRepository.findByName(fileName)
                    .orElseThrow(() -> {
                          log.error("Failed to retrieve file with name: {}", fileName);
                          return new AppException(ErrorCode.FILE_NOT_FOUND);
                    });

            return fileMapper.toFileUploadResponse(fileEntity);
      }

}