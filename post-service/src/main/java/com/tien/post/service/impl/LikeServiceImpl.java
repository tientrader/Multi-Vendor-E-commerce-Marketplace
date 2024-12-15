package com.tien.post.service.impl;

import com.tien.post.entity.Like;
import com.tien.post.entity.Post;
import com.tien.post.exception.AppException;
import com.tien.post.exception.ErrorCode;
import com.tien.post.repository.LikeRepository;
import com.tien.post.repository.PostRepository;
import com.tien.post.service.LikeService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class LikeServiceImpl implements LikeService {

      LikeRepository likeRepository;
      PostRepository postRepository;

      @Override
      public boolean toggleLike(String postId) {
            String username = getCurrentUsername();

            Post post = postRepository.findById(postId)
                    .orElseThrow(() -> new AppException(ErrorCode.POST_NOT_FOUND));

            if (likeRepository.existsByPostIdAndUsername(postId, username)) {
                  likeRepository.deleteByPostIdAndUsername(postId, username);
                  post.setLikesCount(post.getLikesCount() - 1);
                  postRepository.save(post);
                  return false;
            } else {
                  Like like = new Like();
                  like.setPostId(postId);
                  like.setUsername(username);
                  likeRepository.save(like);

                  post.setLikesCount(post.getLikesCount() + 1);
                  postRepository.save(post);
                  return true;
            }
      }

      @Override
      public long getLikesCount(String postId) {
            return likeRepository.countByPostId(postId);
      }

      private String getCurrentUsername() {
            Jwt jwt = (Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            return jwt.getClaim("preferred_username");
      }

}