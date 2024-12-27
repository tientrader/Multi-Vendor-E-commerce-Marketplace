package com.tien.post.repository;

import com.tien.post.entity.Like;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LikeRepository extends MongoRepository<Like, String> {

      long countByPostId(String postId);

      boolean existsByPostIdAndUsername(String postId, String username);

      void deleteByPostIdAndUsername(String postId, String username);

}