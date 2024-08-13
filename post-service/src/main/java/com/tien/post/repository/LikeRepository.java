package com.tien.post.repository;

import com.tien.post.entity.Like;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LikeRepository extends MongoRepository<Like, String> {

      long countByPostId(String postId);

      boolean existsByPostIdAndUserId(String postId, String userId);

      void deleteByPostIdAndUserId(String postId, String userId);

}