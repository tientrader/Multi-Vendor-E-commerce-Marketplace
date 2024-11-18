package com.tien.review.httpclient;

import com.tien.review.configuration.AuthenticationRequestInterceptor;
import com.tien.review.dto.ApiResponse;
import com.tien.review.dto.response.FileResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@FeignClient(name = "file-service", path = "/file", configuration = {AuthenticationRequestInterceptor.class})
public interface FileClient {

      @PostMapping(value = "/multiple-upload", consumes = "multipart/form-data", produces = "application/json")
      ApiResponse<List<FileResponse>> uploadMultipleFiles(@RequestPart("files") List<MultipartFile> files);

}