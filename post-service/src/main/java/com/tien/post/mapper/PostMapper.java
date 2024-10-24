package com.tien.post.mapper;

import com.tien.post.entity.Post;
import com.tien.post.dto.request.PostUpdateRequest;
import com.tien.post.dto.response.PostResponse;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface PostMapper {

      PostResponse toPostResponse(Post post);

      void updatePost(@MappingTarget Post post, PostUpdateRequest postUpdateRequest);

}