package com.tien.post.mapper;

import com.tien.post.dto.request.CommentCreationRequest;
import com.tien.post.dto.request.CommentUpdateRequest;
import com.tien.post.entity.Comment;
import com.tien.post.dto.response.CommentResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface CommentMapper {

      CommentResponse toCommentResponse(Comment comment);

      @Mapping(target = "username", ignore = true)
      @Mapping(target = "modifiedDate", ignore = true)
      @Mapping(target = "id", ignore = true)
      @Mapping(target = "createdDate", ignore = true)
      Comment toComment(CommentCreationRequest request);

      @Mapping(target = "username", ignore = true)
      @Mapping(target = "postId", ignore = true)
      @Mapping(target = "modifiedDate", ignore = true)
      @Mapping(target = "id", ignore = true)
      @Mapping(target = "createdDate", ignore = true)
      void updateCommentFromRequest(CommentUpdateRequest request, @MappingTarget Comment comment);

}