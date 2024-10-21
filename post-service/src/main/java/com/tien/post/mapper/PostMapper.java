package com.tien.post.mapper;

import com.tien.post.entity.Post;
import com.tien.post.dto.request.PostUpdateRequest;
import com.tien.post.dto.response.PostResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface PostMapper {

      @Mapping(target = "created", ignore = true)
      PostResponse toPostResponse(Post post);

      @Mapping(target = "username", ignore = true)
      @Mapping(target = "modifiedDate", ignore = true)
      @Mapping(target = "likesCount", ignore = true)
      @Mapping(target = "id", ignore = true)
      @Mapping(target = "createdDate", ignore = true)
      @Mapping(target = "commentsCount", ignore = true)
      void updatePost(@MappingTarget Post post, PostUpdateRequest postUpdateRequest);

}