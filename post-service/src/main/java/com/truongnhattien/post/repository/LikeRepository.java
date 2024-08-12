package com.truongnhattien.post.repository;

import com.truongnhattien.post.entity.Like;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LikeRepository extends MongoRepository<Like, String> {

      long countByPostId(String postId);

      boolean existsByPostIdAndUserId(String postId, String userId);

      void deleteByPostIdAndUserId(String postId, String userId);

      List<Like> findByPostId(String postId);

}