package com.truongnhattien.post.service;

import com.truongnhattien.post.dto.request.PostCreationRequest;
import com.truongnhattien.post.dto.request.PostUpdateRequest;
import com.truongnhattien.post.dto.response.PostResponse;
import com.truongnhattien.post.entity.Post;
import com.truongnhattien.post.exception.AppException;
import com.truongnhattien.post.exception.ErrorCode;
import com.truongnhattien.post.mapper.PostMapper;
import com.truongnhattien.post.repository.PostRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PostService {

      PostRepository postRepository;
      PostMapper postMapper;
      AuthService authService;

      public PostResponse createPost(PostCreationRequest request) {
            String userId = authService.getAuthenticatedUserId();

            Post post = postRepository.save(Post.builder()
                    .content(request.getContent())
                    .userId(userId)
                    .createdDate(Instant.now())
                    .modifiedDate(Instant.now())
                    .build());

            return postMapper.toPostResponse(post);
      }

      public PostResponse updatePost(String postId, PostUpdateRequest request) {
            String userId = authService.getAuthenticatedUserId();

            Post post = postRepository.findById(postId)
                    .orElseThrow(() -> new AppException(ErrorCode.POST_NOT_FOUND));

            if (!post.getUserId().equals(userId)) {
                  throw new AppException(ErrorCode.UNAUTHENTICATED);
            }

            postMapper.updatePost(post, request);
            post.setModifiedDate(Instant.now());

            postRepository.save(post);

            return postMapper.toPostResponse(post);
      }

      public List<PostResponse> getMyPosts() {
            String userId = authService.getAuthenticatedUserId();

            return postRepository.findAllByUserId(userId)
                    .stream()
                    .map(postMapper::toPostResponse)
                    .toList();
      }

      public PostResponse getPostById(String postId) {
            Post post = postRepository.findById(postId)
                    .orElseThrow(() -> new AppException(ErrorCode.POST_NOT_FOUND));
            return postMapper.toPostResponse(post);
      }

      public void deletePost(String postId) {
            String userId = authService.getAuthenticatedUserId();
            Post post = postRepository.findById(postId)
                    .orElseThrow(() -> new AppException(ErrorCode.POST_NOT_FOUND));

            if (!post.getUserId().equals(userId)) {
                  throw new AppException(ErrorCode.UNAUTHENTICATED);
            }

            postRepository.delete(post);
      }

}