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
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CommentServiceImpl implements CommentService {

      CommentRepository commentRepository;
      CommentMapper commentMapper;
      PostRepository postRepository;
      AuthenticationServiceImpl authenticationService;

      @Override
      public CommentResponse createComment(String postId, CommentCreationRequest request) {
            String username = authenticationService.getAuthenticatedUsername();

            Comment comment = commentMapper.toComment(request);
            comment.setUsername(username);
            comment.setPostId(postId);
            comment.setCreatedDate(Instant.now());
            comment.setModifiedDate(Instant.now());

            Comment savedComment = commentRepository.save(comment);

            Post post = findPostById(postId);
            post.setCommentsCount(post.getCommentsCount() + 1);
            postRepository.save(post);

            return commentMapper.toCommentResponse(savedComment);
      }

      @Override
      public void updateComment(String commentId, CommentUpdateRequest request) {
            String username = authenticationService.getAuthenticatedUsername();

            Comment comment = findCommentById(commentId);
            validateUserOwnership(comment, username);

            commentMapper.updateCommentFromRequest(request, comment);
            comment.setModifiedDate(Instant.now());
            commentRepository.save(comment);
      }

      @Override
      public void deleteComment(String commentId) {
            String username = authenticationService.getAuthenticatedUsername();

            Comment comment = findCommentById(commentId);
            validateUserOwnership(comment, username);

            String postId = comment.getPostId();
            commentRepository.delete(comment);

            Post post = findPostById(postId);
            post.setCommentsCount(post.getCommentsCount() - 1);
            postRepository.save(post);
      }

      @Override
      public List<CommentResponse> getCommentsByPostId(String postId) {
            return commentRepository.findAllByPostId(postId).stream()
                    .map(commentMapper::toCommentResponse)
                    .toList();
      }

      @Override
      public CommentResponse getCommentById(String commentId) {
            return commentMapper.toCommentResponse(findCommentById(commentId));
      }

      private void validateUserOwnership(Comment comment, String username) {
            if (!comment.getUsername().equals(username)) {
                  log.error("User {} is not authorized to update or delete the comment owned by {}.", username, comment.getUsername());
                  throw new AppException(ErrorCode.UNAUTHENTICATED);
            }
      }

      private Post findPostById(String postId) {
            return postRepository.findById(postId)
                    .orElseThrow(() -> new AppException(ErrorCode.POST_NOT_FOUND));
      }

      private Comment findCommentById(String commentId) {
            return commentRepository.findById(commentId)
                    .orElseThrow(() -> new AppException(ErrorCode.COMMENT_NOT_FOUND));
      }

}