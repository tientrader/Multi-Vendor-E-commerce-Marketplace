package com.tien.post.service.impl;

import com.tien.post.dto.PageResponse;
import com.tien.post.entity.Post;
import com.tien.post.exception.AppException;
import com.tien.post.exception.ErrorCode;
import com.tien.post.mapper.PostMapper;
import com.tien.post.dto.request.PostCreationRequest;
import com.tien.post.dto.request.PostUpdateRequest;
import com.tien.post.dto.response.PostResponse;
import com.tien.post.repository.PostRepository;
import com.tien.post.service.PostService;
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
public class PostServiceImpl implements PostService {

      DateTimeFormatterImpl dateTimeFormatter;
      PostRepository postRepository;
      PostMapper postMapper;
      AuthenticationServiceImpl authenticationService;

      @Override
      public PostResponse createPost(PostCreationRequest request) {
            String username = authenticationService.getAuthenticatedUsername();

            Post post = postRepository.save(Post.builder()
                    .content(request.getContent())
                    .username(username)
                    .createdDate(Instant.now())
                    .modifiedDate(Instant.now())
                    .build());

            return postMapper.toPostResponse(post);
      }

      @Override
      public PageResponse<PostResponse> getMyPosts(int page, int size) {
            String username = authenticationService.getAuthenticatedUsername();

            Sort sort = Sort.by("createdDate").descending();
            Pageable pageable = PageRequest.of(page - 1, size, sort);
            var pageData = postRepository.findAllByUsername(username, pageable);

            var postList = pageData.getContent().stream().map(post -> {
                  var postResponse = postMapper.toPostResponse(post);
                  postResponse.setUsername(username);
                  postResponse.setCreated(dateTimeFormatter.format(post.getCreatedDate()));
                  return postResponse;
            }).toList();

            return PageResponse.<PostResponse>builder()
                    .currentPage(page)
                    .pageSize(pageData.getSize())
                    .totalPages(pageData.getTotalPages())
                    .totalElements(pageData.getTotalElements())
                    .data(postList)
                    .build();
      }

      @Override
      public PostResponse getPostById(String postId) {
            Post post = postRepository.findById(postId)
                    .orElseThrow(() -> new AppException(ErrorCode.POST_NOT_FOUND));
            return postMapper.toPostResponse(post);
      }

      @Override
      public PostResponse updatePost(String postId, PostUpdateRequest request) {
            String username = authenticationService.getAuthenticatedUsername();

            Post post = postRepository.findById(postId)
                    .orElseThrow(() -> new AppException(ErrorCode.POST_NOT_FOUND));

            if (!post.getUsername().equals(username)) {
                  throw new AppException(ErrorCode.UNAUTHENTICATED);
            }

            postMapper.updatePost(post, request);
            post.setModifiedDate(Instant.now());

            postRepository.save(post);

            return postMapper.toPostResponse(post);
      }

      @Override
      public void deletePost(String postId) {
            String username = authenticationService.getAuthenticatedUsername();
            Post post = postRepository.findById(postId)
                    .orElseThrow(() -> new AppException(ErrorCode.POST_NOT_FOUND));

            if (!post.getUsername().equals(username)) {
                  throw new AppException(ErrorCode.UNAUTHENTICATED);
            }

            postRepository.delete(post);
      }

}