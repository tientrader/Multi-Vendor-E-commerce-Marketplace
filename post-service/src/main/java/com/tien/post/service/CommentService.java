package com.tien.post.service;

import com.tien.post.dto.request.CommentCreationRequest;
import com.tien.post.dto.request.CommentUpdateRequest;
import com.tien.post.dto.response.CommentResponse;

import java.util.List;

public interface CommentService {

      CommentResponse createComment(String postId, CommentCreationRequest request);

      void updateComment(String commentId, CommentUpdateRequest request);

      void deleteComment(String commentId);

      List<CommentResponse> getCommentsByPostId(String postId);

      CommentResponse getCommentById(String commentId);

}