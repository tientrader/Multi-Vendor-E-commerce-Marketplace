package com.tien.post.repository;

import com.tien.post.entity.Comment;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends MongoRepository<Comment, String> {

      List<Comment> findAllByPostId(String postId);

}