package com.truongnhattien.post.repository;

import com.truongnhattien.post.entity.Post;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends MongoRepository<Post, String> {

      List<Post> findAllByUserId(String userId);

}