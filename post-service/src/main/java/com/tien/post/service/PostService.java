package com.tien.post.service;

import com.tien.post.dto.PageResponse;
import com.tien.post.dto.request.PostCreationRequest;
import com.tien.post.dto.request.PostUpdateRequest;
import com.tien.post.dto.response.PostResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface PostService {

      PostResponse createPost(PostCreationRequest request, List<MultipartFile> postImages);

      PostResponse updatePost(String postId, PostUpdateRequest request, List<MultipartFile> newImages);

      void deletePost(String postId);

      PageResponse<PostResponse> searchPosts(String keyword, int page, int size);

      PageResponse<PostResponse> getHomePagePosts(int page, int size);

      PageResponse<PostResponse> getMyPosts(int page, int size);

      PostResponse getPostById(String postId);

}