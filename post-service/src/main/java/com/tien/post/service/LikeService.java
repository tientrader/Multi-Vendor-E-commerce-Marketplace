package com.tien.post.service;

public interface LikeService {

      void addLike(String postId);
      void removeLike(String postId);
      long getLikesCount(String postId);
      boolean hasLiked(String postId);

}