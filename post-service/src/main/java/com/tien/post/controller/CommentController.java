package com.tien.post.controller;

import com.tien.post.dto.ApiResponse;
import com.tien.post.dto.request.CommentCreationRequest;
import com.tien.post.dto.request.CommentUpdateRequest;
import com.tien.post.dto.response.CommentResponse;
import com.tien.post.service.CommentService;
import jakarta.validation.Valid;
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
      public ApiResponse<CommentResponse> createComment(@PathVariable String postId,
                                                        @RequestBody @Valid CommentCreationRequest request) {
            return ApiResponse.<CommentResponse>builder()
                    .result(commentService.createComment(postId, request))
                    .build();
      }

      @PutMapping("/{commentId}")
      public ApiResponse<Void> updateComment(@PathVariable String commentId,
                                             @RequestBody @Valid CommentUpdateRequest request) {
            commentService.updateComment(commentId, request);
            return ApiResponse.<Void>builder()
                    .message("Comment updated successfully")
                    .build();
      }

      @DeleteMapping("/{commentId}")
      public ApiResponse<Void> deleteComment(@PathVariable String commentId) {
            commentService.deleteComment(commentId);
            return ApiResponse.<Void>builder()
                    .message("Comment deleted successfully")
                    .build();
      }

      @GetMapping("/post/{postId}")
      public ApiResponse<List<CommentResponse>> getCommentsByPostId(@PathVariable String postId) {
            return ApiResponse.<List<CommentResponse>>builder()
                    .result(commentService.getCommentsByPostId(postId))
                    .build();
      }

      @GetMapping("/{commentId}")
      public ApiResponse<CommentResponse> getCommentById(@PathVariable String commentId) {
            return ApiResponse.<CommentResponse>builder()
                    .result(commentService.getCommentById(commentId))
                    .build();
      }

}