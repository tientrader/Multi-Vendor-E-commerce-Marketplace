package com.tien.post.mapper;

import com.tien.post.dto.request.PostCreationRequest;
import com.tien.post.entity.Post;
import com.tien.post.dto.request.PostUpdateRequest;
import com.tien.post.dto.response.PostResponse;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PostMapper {

      Post toPost(PostCreationRequest request);

      PostResponse toPostResponse(Post post);

      List<PostResponse> toPostResponseList(List<Post> posts);

      void updatePost(@MappingTarget Post post, PostUpdateRequest postUpdateRequest);

}