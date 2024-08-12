package com.truongnhattien.post.service;

import com.truongnhattien.post.dto.request.CommentCreationRequest;
import com.truongnhattien.post.dto.request.CommentUpdateRequest;
import com.truongnhattien.post.dto.response.CommentResponse;
import com.truongnhattien.post.entity.Comment;
import com.truongnhattien.post.entity.Post;
import com.truongnhattien.post.exception.AppException;
import com.truongnhattien.post.exception.ErrorCode;
import com.truongnhattien.post.mapper.CommentMapper;
import com.truongnhattien.post.repository.CommentRepository;
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
public class CommentService {

      CommentRepository commentRepository;
      CommentMapper commentMapper;
      PostRepository postRepository;
      AuthService authService;

      public CommentResponse createComment(String postId, CommentCreationRequest request) {
            String userId = authService.getAuthenticatedUserId();

            postRepository.findById(postId)
                    .orElseThrow(() -> new AppException(ErrorCode.POST_NOT_FOUND));

            Comment comment = commentMapper.toComment(request);
            comment.setUserId(userId);
            comment.setPostId(postId);
            comment.setCreatedDate(Instant.now());
            comment.setModifiedDate(Instant.now());

            Comment savedComment = commentRepository.save(comment);

            Post post = postRepository.findById(postId)
                    .orElseThrow(() -> new AppException(ErrorCode.POST_NOT_FOUND));
            post.setCommentsCount(post.getCommentsCount() + 1);
            postRepository.save(post);

            return commentMapper.toCommentResponse(savedComment);
      }

      public List<CommentResponse> getCommentsByPostId(String postId) {
            return commentRepository.findAllByPostId(postId)
                    .stream()
                    .map(commentMapper::toCommentResponse)
                    .toList();
      }

      public CommentResponse getCommentById(String commentId) {
            Comment comment = commentRepository.findById(commentId)
                    .orElseThrow(() -> new AppException(ErrorCode.COMMENT_NOT_FOUND));
            return commentMapper.toCommentResponse(comment);
      }

      public void updateComment(String commentId, CommentUpdateRequest request) {
            String userId = authService.getAuthenticatedUserId();

            Comment comment = commentRepository.findById(commentId)
                    .orElseThrow(() -> new AppException(ErrorCode.COMMENT_NOT_FOUND));

            if (!comment.getUserId().equals(userId)) {
                  throw new AppException(ErrorCode.UNAUTHENTICATED);
            }

            commentMapper.updateCommentFromRequest(request, comment);
            comment.setModifiedDate(Instant.now());
            commentRepository.save(comment);
      }

      public void deleteComment(String commentId) {
            String userId = authService.getAuthenticatedUserId();

            Comment comment = commentRepository.findById(commentId)
                    .orElseThrow(() -> new AppException(ErrorCode.COMMENT_NOT_FOUND));

            if (!comment.getUserId().equals(userId)) {
                  throw new AppException(ErrorCode.UNAUTHENTICATED);
            }

            String postId = comment.getPostId();
            commentRepository.delete(comment);

            Post post = postRepository.findById(postId)
                    .orElseThrow(() -> new AppException(ErrorCode.POST_NOT_FOUND));
            post.setCommentsCount(post.getCommentsCount() - 1);
            postRepository.save(post);
      }

}