package com.tien.post.controller;

import com.tien.post.dto.ApiResponse;
import com.tien.post.service.LikeService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/likes")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class LikeController {

      LikeService likeService;

      @PostMapping("/toggle/{postId}")
      public ApiResponse<String> toggleLike(@PathVariable String postId) {
            boolean isLiked = likeService.toggleLike(postId);
            return ApiResponse.<String>builder()
                    .result(isLiked ? "Like added successfully" : "Like removed successfully")
                    .build();
      }

      @GetMapping("/count/{postId}")
      public ApiResponse<Long> getLikesCount(@PathVariable String postId) {
            return ApiResponse.<Long>builder()
                    .result(likeService.getLikesCount(postId))
                    .build();
      }

}