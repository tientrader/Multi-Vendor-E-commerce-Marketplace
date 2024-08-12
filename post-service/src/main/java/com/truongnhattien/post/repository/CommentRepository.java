package com.truongnhattien.post.repository;

import com.truongnhattien.post.entity.Comment;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends MongoRepository<Comment, String> {

      List<Comment> findAllByPostId(String postId);

}