package com.tien.post.service.impl;

import com.tien.post.dto.request.CommentCreationRequest;
import com.tien.post.dto.request.CommentUpdateRequest;
import com.tien.post.dto.response.CommentResponse;
import com.tien.post.entity.Comment;
import com.tien.post.entity.Post;
import com.tien.post.exception.AppException;
import com.tien.post.exception.ErrorCode;
import com.tien.post.mapper.CommentMapper;
import com.tien.post.repository.CommentRepository;
import com.tien.post.repository.PostRepository;
import com.tien.post.service.CommentService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CommentServiceImpl implements CommentService {

      CommentRepository commentRepository;
      CommentMapper commentMapper;
      PostRepository postRepository;
      AuthenticationServiceImpl authenticationService;

      public CommentResponse createComment(String postId, CommentCreationRequest request) {
            String userId = authenticationService.getAuthenticatedUserId();

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
            String userId = authenticationService.getAuthenticatedUserId();

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
            String userId = authenticationService.getAuthenticatedUserId();

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