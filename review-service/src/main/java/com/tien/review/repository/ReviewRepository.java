package com.tien.review.repository;

import com.tien.review.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends MongoRepository<Review, String> {

      List<Review> findByProductId(String productId);

      List<Review> findByUsername(String username);

      Page<Review> findByProductIdAndRating(String productId, int rating, Pageable pageable);

      boolean existsByProductIdAndUsername(String productId, String username);

}