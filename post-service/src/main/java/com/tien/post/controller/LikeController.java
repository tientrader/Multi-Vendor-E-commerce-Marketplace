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

      @PostMapping("/add")
      public ApiResponse<String> addLike(@RequestParam String postId) {
            likeService.addLike(postId);
            return ApiResponse.<String>builder()
                    .result("Like added successfully")
                    .build();
      }

      @PostMapping("/remove")
      public ApiResponse<String> removeLike(@RequestParam String postId) {
            likeService.removeLike(postId);
            return ApiResponse.<String>builder()
                    .result("Like removed successfully")
                    .build();
      }

      @GetMapping("/count")
      public ApiResponse<Long> getLikesCount(@RequestParam String postId) {
            long count = likeService.getLikesCount(postId);
            return ApiResponse.<Long>builder()
                    .result(count)
                    .build();
      }

      @GetMapping("/hasLiked")
      public ApiResponse<Boolean> hasLiked(@RequestParam String postId) {
            boolean hasLiked = likeService.hasLiked(postId);
            return ApiResponse.<Boolean>builder()
                    .result(hasLiked)
                    .build();
      }

}