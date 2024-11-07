package com.tien.file.service;

import com.tien.file.dto.response.FileResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface FileService {

      FileResponse uploadFile(MultipartFile file) throws IOException;

      List<FileResponse> uploadMultipleFiles(List<MultipartFile> files) throws IOException;

      void deleteFile(String fileName);

      FileResponse getFile(String fileName);

}