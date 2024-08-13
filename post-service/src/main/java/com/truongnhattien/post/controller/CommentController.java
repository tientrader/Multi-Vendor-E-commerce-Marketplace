package com.truongnhattien.post.controller;

import com.truongnhattien.post.dto.ApiResponse;
import com.truongnhattien.post.dto.request.CommentCreationRequest;
import com.truongnhattien.post.dto.request.CommentUpdateRequest;
import com.truongnhattien.post.dto.response.CommentResponse;
import com.truongnhattien.post.service.CommentService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/comments")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CommentController {

      CommentService commentService;

      @PostMapping("/{postId}")
      public ApiResponse<CommentResponse> createComment(@PathVariable String postId, @RequestBody CommentCreationRequest request) {
            request.setPostId(postId);
            return ApiResponse.<CommentResponse>builder()
                    .result(commentService.createComment(postId, request))
                    .build();
      }

      @GetMapping("/{commentId}")
      public ApiResponse<CommentResponse> getCommentById(@PathVariable String commentId) {
            return ApiResponse.<CommentResponse>builder()
                    .result(commentService.getCommentById(commentId))
                    .build();
      }

      @PutMapping("/{commentId}")
      public ApiResponse<Void> updateComment(@PathVariable String commentId, @RequestBody CommentUpdateRequest request) {
            commentService.updateComment(commentId, request);
            return ApiResponse.<Void>builder()
                    .build();
      }

      @DeleteMapping("/{commentId}")
      public ApiResponse<Void> deleteComment(@PathVariable String commentId) {
            commentService.deleteComment(commentId);
            return ApiResponse.<Void>builder()
                    .build();
      }

      @GetMapping("/post/{postId}")
      public ApiResponse<List<CommentResponse>> getCommentsByPostId(@PathVariable String postId) {
            return ApiResponse.<List<CommentResponse>>builder()
                    .result(commentService.getCommentsByPostId(postId))
                    .build();
      }

}