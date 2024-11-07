package com.tien.file.controller;

import com.tien.file.dto.ApiResponse;
import com.tien.file.dto.response.FileResponse;
import com.tien.file.service.FileService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class FileController {

      FileService fileService;

      @PostMapping("/upload")
      public ApiResponse<FileResponse> uploadFile(@RequestParam("file") MultipartFile file) throws IOException {
            return ApiResponse.<FileResponse>builder()
                    .result(fileService.uploadFile(file))
                    .build();
      }

      @PostMapping("/multiple-upload")
      public ApiResponse<List<FileResponse>> uploadMultipleFiles(@RequestPart("files") List<MultipartFile> files) throws IOException {
            return ApiResponse.<List<FileResponse>>builder()
                    .result(fileService.uploadMultipleFiles(files))
                    .build();
      }

      @DeleteMapping("/delete")
      public ApiResponse<String> deleteFile(@RequestParam("fileName") String fileName) {
            fileService.deleteFile(fileName);
            return ApiResponse.<String>builder()
                    .result("File deleted successfully")
                    .build();
      }

      @GetMapping
      public ApiResponse<FileResponse> getFile(@RequestParam("fileName") String fileName) {
            return ApiResponse.<FileResponse>builder()
                    .result(fileService.getFile(fileName))
                    .build();
      }

}