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
import com.tien.post.util.DateTimeFormatter;
import feign.FeignException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.Instant;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PostServiceImpl implements PostService {

      UserClient userClient;
      FileClient fileClient;
      DateTimeFormatter dateTimeFormatter;
      PostRepository postRepository;
      PostMapper postMapper;

      @Override
      public PostResponse createPost(PostCreationRequest request, List<MultipartFile> postImages) {
            String username = getCurrentUsername();

            checkIfUserIsVIP(username);
            List<String> imageUrls = uploadImages(postImages);

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
            String username = getCurrentUsername();

            Post post = postRepository.findById(postId)
                    .orElseThrow(() -> new AppException(ErrorCode.POST_NOT_FOUND));

            if (!post.getUsername().equals(username)) {
                  log.error("User {} is not authorized to access the post owned by {}.", username, post.getUsername());
                  throw new AppException(ErrorCode.UNAUTHENTICATED);
            }

            postMapper.updatePost(post, request);
            post.setModifiedDate(Instant.now());

            List<String> imageUrls = uploadImages(postImages);
            if (!imageUrls.isEmpty()) {
                  post.setImageUrls(imageUrls);
            }

            postRepository.save(post);
            return postMapper.toPostResponse(post);
      }

      @Override
      public void deletePost(String postId) {
            String username = getCurrentUsername();
            Post post = postRepository.findById(postId)
                    .orElseThrow(() -> new AppException(ErrorCode.POST_NOT_FOUND));

            if (!post.getUsername().equals(username)) {
                  log.error("User {} is not authorized to delete the post owned by {}.", username, post.getUsername());
                  throw new AppException(ErrorCode.UNAUTHENTICATED);
            }

            postRepository.delete(post);
      }

      @Override
      public PageResponse<PostResponse> searchPosts(String keyword, int page, int size) {
            Pageable pageable = PageRequest.of(page - 1, size);

            var pageData = postRepository.searchPosts(keyword, pageable);
            var postList = postMapper.toPostResponseList(pageData.getContent());

            return PageResponse.<PostResponse>builder()
                    .currentPage(page)
                    .pageSize(pageData.getSize())
                    .totalPages(pageData.getTotalPages())
                    .totalElements(pageData.getTotalElements())
                    .data(postList)
                    .build();
      }

      @Override
      public PageResponse<PostResponse> getHomePagePosts(int page, int size) {
            Sort sort = Sort.by(Sort.Direction.DESC, "createdDate");
            Pageable pageable = PageRequest.of(page - 1, size, sort);

            var pageData = postRepository.findAll(pageable);
            var postList = pageData.getContent().stream().map(post -> {
                  var postResponse = postMapper.toPostResponse(post);
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
      public PageResponse<PostResponse> getMyPosts(int page, int size) {
            String username = getCurrentUsername();

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

      private String getCurrentUsername() {
            Jwt jwt = (Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            return jwt.getClaim("preferred_username");
      }

      private void checkIfUserIsVIP(String username) {
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
      }

      private List<String> uploadImages(List<MultipartFile> postImages) {
            if (postImages == null || postImages.isEmpty()) {
                  return List.of();
            }

            try {
                  ApiResponse<List<FileResponse>> fileResponseApi = fileClient.uploadMultipleFiles(postImages);

                  if (fileResponseApi == null || fileResponseApi.getResult() == null) {
                        log.error("File upload returned no results");
                        throw new AppException(ErrorCode.FILE_UPLOAD_FAILED);
                  }

                  return fileResponseApi.getResult()
                          .stream()
                          .map(FileResponse::getUrl)
                          .toList();
            } catch (FeignException e) {
                  log.error("Error uploading files: {}", e.getMessage(), e);
                  throw new AppException(ErrorCode.SERVICE_UNAVAILABLE);
            }
      }

}