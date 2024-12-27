package com.tien.post.repository;

import com.tien.post.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRepository extends MongoRepository<Post, String> {

      @Query("{ $or: [ { 'username': { $regex: ?0, $options: 'i' } }, { 'content': { $regex: ?0, $options: 'i' } } ] }")
      Page<Post> searchPosts(String keyword, Pageable pageable);

      Page<Post> findAllByUsername(String username, Pageable pageable);

}