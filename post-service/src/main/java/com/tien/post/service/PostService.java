package com.tien.post.service;

import com.tien.post.dto.PageResponse;
import com.tien.post.dto.request.PostCreationRequest;
import com.tien.post.dto.request.PostUpdateRequest;
import com.tien.post.dto.response.PostResponse;

public interface PostService {

      PostResponse createPost(PostCreationRequest request);
      PageResponse<PostResponse> getMyPosts(int page, int size);
      PostResponse getPostById(String postId);
      PostResponse updatePost(String postId, PostUpdateRequest request);
      void deletePost(String postId);

}