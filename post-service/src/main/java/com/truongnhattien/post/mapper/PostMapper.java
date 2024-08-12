package com.truongnhattien.post.mapper;

import com.truongnhattien.post.dto.request.PostUpdateRequest;
import com.truongnhattien.post.dto.response.PostResponse;
import com.truongnhattien.post.entity.Post;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface PostMapper {

    PostResponse toPostResponse(Post post);

    void updatePost(@MappingTarget Post post, PostUpdateRequest postUpdateRequest);

}