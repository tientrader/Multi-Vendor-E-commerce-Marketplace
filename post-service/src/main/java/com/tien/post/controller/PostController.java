package com.tien.post.controller;

import com.tien.post.dto.ApiResponse;
import com.tien.post.dto.request.PostCreationRequest;
import com.tien.post.dto.request.PostUpdateRequest;
import com.tien.post.dto.response.PostResponse;
import com.tien.post.service.PostService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/posts")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PostController {

      PostService postService;

      @PostMapping("/create")
      public ApiResponse<PostResponse> createPost(@RequestBody PostCreationRequest request) {
            return ApiResponse.<PostResponse>builder()
                    .result(postService.createPost(request))
                    .build();
      }

      @GetMapping("/my-posts")
      public ApiResponse<List<PostResponse>> myPosts() {
            return ApiResponse.<List<PostResponse>>builder()
                    .result(postService.getMyPosts())
                    .build();
      }

      @GetMapping("/{postId}")
      public ApiResponse<PostResponse> getPostById(@PathVariable String postId) {
            return ApiResponse.<PostResponse>builder()
                    .result(postService.getPostById(postId))
                    .build();
      }

      @PutMapping("/{postId}")
      public ApiResponse<PostResponse> updatePost(@PathVariable String postId, @RequestBody PostUpdateRequest request) {
            return ApiResponse.<PostResponse>builder()
                    .result(postService.updatePost(postId, request))
                    .build();
      }

      @DeleteMapping("/{postId}")
      public ApiResponse<Void> deletePost(@PathVariable String postId) {
            postService.deletePost(postId);
            return ApiResponse.<Void>builder()
                    .message("Post deleted successfully")
                    .build();
      }

}