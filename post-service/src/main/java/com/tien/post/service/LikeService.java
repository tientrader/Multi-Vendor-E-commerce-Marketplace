package com.tien.post.service;

public interface LikeService {

      boolean toggleLike(String postId);
      long getLikesCount(String postId);
      boolean hasLiked(String postId);

}