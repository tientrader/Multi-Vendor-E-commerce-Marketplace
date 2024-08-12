package com.truongnhattien.post.mapper;

import com.truongnhattien.post.dto.request.CommentCreationRequest;
import com.truongnhattien.post.dto.request.CommentUpdateRequest;
import com.truongnhattien.post.dto.response.CommentResponse;
import com.truongnhattien.post.entity.Comment;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface CommentMapper {

    CommentResponse toCommentResponse(Comment comment);

    Comment toComment(CommentCreationRequest request);

    void updateCommentFromRequest(CommentUpdateRequest request, @MappingTarget Comment comment);

}