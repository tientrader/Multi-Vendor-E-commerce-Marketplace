package com.tien.post.service.impl;

import com.tien.post.dto.ApiResponse;
import com.tien.post.dto.PageResponse;
import com.tien.post.httpclient.response.FileResponse;
import com.tien.post.entity.Post;
import com.tien.post.exception.AppException;
import com.tien.post.exception.ErrorCode;
import com.tien.post.httpclient.FileClient;
import com.tien.post.httpclient.UserClient;
import com.tien.post.mapper.PostMapper;
import com.tien.post.dto.request.PostCreationRequest;
import com.tien.post.dto.request.PostUpdateRequest;
import com.tien.post.dto.response.PostResponse;
import com.tien.post.repository.PostRepository;
import com.tien.post.service.PostService;
import feign.FeignException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PostServiceImpl implements PostService {

      UserClient userClient;
      FileClient fileClient;
      DateTimeFormatterImpl dateTimeFormatter;
      PostRepository postRepository;
      PostMapper postMapper;
      AuthenticationServiceImpl authenticationService;

      @Override
      public PostResponse createPost(PostCreationRequest request, List<MultipartFile> postImages) {
            String username = authenticationService.getAuthenticatedUsername();

            try {
                  userClient.checkIfUserIsVIP(username);
            } catch (FeignException e) {
                  if (e.status() == 400 && e.getMessage().contains("USER_NOT_VIP")) {
                        log.error("User {} is not VIP.", username);
                        throw new AppException(ErrorCode.USER_NOT_VIP);
                  }
                  log.error("External service error while checking VIP status for user {}: {}", username, e.getMessage());
                  throw new AppException(ErrorCode.SERVICE_UNAVAILABLE);
            }

            List<String> imageUrls = handleImageUpload(postImages);

            Post post = postMapper.toPost(request);
            post.setUsername(username);
            post.setCreatedDate(Instant.now());
            post.setModifiedDate(Instant.now());
            post.setImageUrls(imageUrls);

            post = postRepository.save(post);
            return postMapper.toPostResponse(post);
      }

      @Override
      public PostResponse updatePost(String postId, PostUpdateRequest request, List<MultipartFile> postImages) {
            String username = authenticationService.getAuthenticatedUsername();

            Post post = findPostById(postId);
            validateUserOwnership(post, username);

            postMapper.updatePost(post, request);
            post.setModifiedDate(Instant.now());

            List<String> imageUrls = handleImageUpload(postImages);
            if (!imageUrls.isEmpty()) {
                  post.setImageUrls(imageUrls);
            }

            postRepository.save(post);
            return postMapper.toPostResponse(post);
      }

      @Override
      public void deletePost(String postId) {
            String username = authenticationService.getAuthenticatedUsername();
            Post post = findPostById(postId);
            validateUserOwnership(post, username);

            postRepository.delete(post);
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
            return postMapper.toPostResponse(findPostById(postId));
      }

      private List<String> handleImageUpload(List<MultipartFile> postImages) {
            if (postImages == null || postImages.isEmpty()) {
                  return List.of();
            }

            ApiResponse<List<FileResponse>> fileResponseApi;
            try {
                  fileResponseApi = fileClient.uploadMultipleFiles(postImages);

                  if (fileResponseApi == null || fileResponseApi.getResult() == null) {
                        log.error("File upload returned no results");
                        throw new AppException(ErrorCode.FILE_UPLOAD_FAILED);
                  }
            } catch (FeignException e) {
                  log.error("Error uploading files: {}", e.getMessage(), e);
                  throw new AppException(ErrorCode.SERVICE_UNAVAILABLE);
            }

            List<FileResponse> fileResponses = fileResponseApi.getResult();

            return fileResponses.stream()
                    .map(FileResponse::getUrl)
                    .collect(Collectors.toList());
      }

      private Post findPostById(String postId) {
            return postRepository.findById(postId)
                    .orElseThrow(() -> {
                          log.error("Post with ID {} not found.", postId);
                          return new AppException(ErrorCode.POST_NOT_FOUND);
                    });
      }

      private void validateUserOwnership(Post post, String username) {
            if (!post.getUsername().equals(username)) {
                  log.error("User {} is not authorized to access the post owned by {}.", username, post.getUsername());
                  throw new AppException(ErrorCode.UNAUTHENTICATED);
            }
      }

}