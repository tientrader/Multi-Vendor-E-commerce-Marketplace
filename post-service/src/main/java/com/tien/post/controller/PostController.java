package com.tien.post.controller;

import com.tien.post.dto.ApiResponse;
import com.tien.post.dto.PageResponse;
import com.tien.post.dto.request.PostCreationRequest;
import com.tien.post.dto.request.PostUpdateRequest;
import com.tien.post.dto.response.PostResponse;
import com.tien.post.service.PostService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/posts")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PostController {

      PostService postService;

      @PostMapping("/create")
      public ApiResponse<PostResponse> createPost(
              @RequestPart("request") @Valid PostCreationRequest request,
              @RequestPart(value = "postImages", required = false) List<MultipartFile> postImages) {
            return ApiResponse.<PostResponse>builder()
                    .result(postService.createPost(request, postImages))
                    .build();
      }

      @PutMapping("/{postId}")
      public ApiResponse<PostResponse> updatePost(
              @PathVariable String postId,
              @RequestPart("request") @Valid PostUpdateRequest request,
              @RequestPart(value = "postImages", required = false) List<MultipartFile> postImages) {
            return ApiResponse.<PostResponse>builder()
                    .result(postService.updatePost(postId, request, postImages))
                    .build();
      }

      @DeleteMapping("/{postId}")
      public ApiResponse<Void> deletePost(@PathVariable String postId) {
            postService.deletePost(postId);
            return ApiResponse.<Void>builder()
                    .message("Post deleted successfully")
                    .build();
      }

      @GetMapping("/my-posts")
      public ApiResponse<PageResponse<PostResponse>> myPosts(
              @RequestParam(value = "page", required = false, defaultValue = "1") int page,
              @RequestParam(value = "size", required = false, defaultValue = "10") int size) {
            return ApiResponse.<PageResponse<PostResponse>>builder()
                    .result(postService.getMyPosts(page, size))
                    .build();
      }

      @GetMapping("/{postId}")
      public ApiResponse<PostResponse> getPostById(@PathVariable String postId) {
            return ApiResponse.<PostResponse>builder()
                    .result(postService.getPostById(postId))
                    .build();
      }

}