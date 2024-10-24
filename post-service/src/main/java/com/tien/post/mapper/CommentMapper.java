package com.tien.post.mapper;

import com.tien.post.dto.request.CommentCreationRequest;
import com.tien.post.dto.request.CommentUpdateRequest;
import com.tien.post.entity.Comment;
import com.tien.post.dto.response.CommentResponse;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface CommentMapper {

      CommentResponse toCommentResponse(Comment comment);

      Comment toComment(CommentCreationRequest request);

      void updateCommentFromRequest(CommentUpdateRequest request, @MappingTarget Comment comment);

}