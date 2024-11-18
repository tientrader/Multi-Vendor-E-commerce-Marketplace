package com.tien.review.mapper;

import com.tien.review.dto.request.ReviewCreationRequest;
import com.tien.review.dto.request.ReviewUpdateRequest;
import com.tien.review.dto.response.ReviewResponse;
import com.tien.review.entity.Review;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ReviewMapper {

      Review toReview(ReviewCreationRequest request);

      ReviewResponse toReviewResponse(Review review);

      void updateReview(@MappingTarget Review review, ReviewUpdateRequest request);

}