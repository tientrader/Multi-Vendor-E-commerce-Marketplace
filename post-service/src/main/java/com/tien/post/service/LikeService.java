package com.tien.post.service;

import com.tien.post.entity.Like;
import com.tien.post.entity.Post;
import com.tien.post.exception.AppException;
import com.tien.post.exception.ErrorCode;
import com.tien.post.repository.LikeRepository;
import com.tien.post.repository.PostRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class LikeService {

      LikeRepository likeRepository;
      PostRepository postRepository;
      AuthService authService;

      public void addLike(String postId) {
            String userId = authService.getAuthenticatedUserId();

            Post post = postRepository.findById(postId)
                    .orElseThrow(() -> new AppException(ErrorCode.POST_NOT_FOUND));

            if (!likeRepository.existsByPostIdAndUserId(postId, userId)) {
                  Like like = new Like();
                  like.setPostId(postId);
                  like.setUserId(userId);
                  likeRepository.save(like);

                  post.setLikesCount(post.getLikesCount() + 1);
                  postRepository.save(post);
            }
      }

      public void removeLike(String postId) {
            String userId = authService.getAuthenticatedUserId();

            if (likeRepository.existsByPostIdAndUserId(postId, userId)) {
                  likeRepository.deleteByPostIdAndUserId(postId, userId);

                  Post post = postRepository.findById(postId)
                          .orElseThrow(() -> new AppException(ErrorCode.POST_NOT_FOUND));
                  post.setLikesCount(post.getLikesCount() - 1);
                  postRepository.save(post);
            } else {
                  throw new AppException(ErrorCode.LIKE_NOT_FOUND);
            }
      }

      public long getLikesCount(String postId) {
            return likeRepository.countByPostId(postId);
      }

      public boolean hasLiked(String postId) {
            String userId = authService.getAuthenticatedUserId();
            return likeRepository.existsByPostIdAndUserId(postId, userId);
      }

}