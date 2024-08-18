package com.tien.post.service;

import com.tien.post.dto.PageResponse;
import com.tien.post.entity.Post;
import com.tien.post.exception.AppException;
import com.tien.post.exception.ErrorCode;
import com.tien.post.mapper.PostMapper;
import com.tien.post.dto.request.PostCreationRequest;
import com.tien.post.dto.request.PostUpdateRequest;
import com.tien.post.dto.response.PostResponse;
import com.tien.post.repository.PostRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.Instant;

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

      public PageResponse<PostResponse> getMyPosts(int page, int size) {
            String userId = authService.getAuthenticatedUserId();

            Sort sort = Sort.by("createdDate").descending();
            Pageable pageable = PageRequest.of(page - 1, size, sort);
            var pageData = postRepository.findAllByUserId(userId, pageable);

            return PageResponse.<PostResponse>builder()
                    .currentPage(page)
                    .pageSize(pageData.getSize())
                    .totalPages(pageData.getTotalPages())
                    .totalElements(pageData.getTotalElements())
                    .data(pageData.getContent().stream().map(postMapper::toPostResponse).toList())
                    .build();
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