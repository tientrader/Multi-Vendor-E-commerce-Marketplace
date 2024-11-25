package com.tien.review.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tien.review.dto.ApiResponse;
import com.tien.review.dto.request.ReviewCreationRequest;
import com.tien.review.dto.request.ReviewUpdateRequest;
import com.tien.review.dto.response.*;
import com.tien.review.entity.Review;
import com.tien.review.exception.AppException;
import com.tien.review.exception.ErrorCode;
import com.tien.review.httpclient.FileClient;
import com.tien.review.httpclient.OrderClient;
import com.tien.review.httpclient.ProductClient;
import com.tien.review.httpclient.ShopClient;
import com.tien.review.httpclient.response.FileResponse;
import com.tien.review.httpclient.response.OrderResponse;
import com.tien.review.httpclient.response.ProductResponse;
import com.tien.review.httpclient.response.ShopResponse;
import com.tien.review.mapper.ReviewMapper;
import com.tien.review.repository.ReviewRepository;
import com.tien.review.service.ReviewService;
import feign.FeignException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ReviewServiceImpl implements ReviewService {

      ReviewRepository reviewRepository;
      ReviewMapper reviewMapper;
      FileClient fileClient;
      OrderClient orderClient;
      ProductClient productClient;
      ShopClient shopClient;

      @Override
      public ReviewResponse createReview(ReviewCreationRequest request, List<MultipartFile> images) {
            String username = getCurrentUsername();

            if (reviewRepository.existsByProductIdAndUsername(request.getProductId(), username)) {
                  log.error("User {} has already reviewed product {}.", username, request.getProductId());
                  throw new AppException(ErrorCode.ALREADY_REVIEWED);
            }

            try {
                  var existsResponse = productClient.existsProduct(request.getProductId(), request.getVariantId());
                  if (!existsResponse.getResult().isExists()) {
                        log.error("Product with ID {} and variant {} does not exist.",
                                request.getProductId(), request.getVariantId());
                        throw new AppException(ErrorCode.PRODUCT_NOT_FOUND);
                  }
            } catch (FeignException e) {
                  if (e.status() == 404) {
                        log.error("Product not found (404): {}", e.getMessage());
                        throw new AppException(ErrorCode.PRODUCT_NOT_FOUND);
                  } else {
                        log.error("Error checking product existence: {}", e.getMessage());
                        throw new AppException(ErrorCode.SERVICE_UNAVAILABLE);
                  }
            }

            try {
                  var ordersResponse = orderClient.getMyOrders();
                  ObjectMapper objectMapper = new ObjectMapper();
                  List<OrderResponse> orders = objectMapper.convertValue(ordersResponse.getResult(), new TypeReference<>() {});

                  boolean hasPurchased = orders.stream()
                          .flatMap(order -> order.getItems().stream())
                          .anyMatch(item -> item.getProductId().equals(request.getProductId()) &&
                                  item.getVariantId().equals(request.getVariantId()));

                  if (!hasPurchased) {
                        log.error("User {} has not purchased the product {} with variant {}",
                                username, request.getProductId(), request.getVariantId());
                        throw new AppException(ErrorCode.UNAUTHORIZED);
                  }
            } catch (FeignException e) {
                  if (e.status() == 404) {
                        log.error("Orders not found for user (404): {}", e.getMessage());
                        throw new AppException(ErrorCode.USER_NOT_PURCHASED_PRODUCT);
                  } else {
                        log.error("Error fetching user orders: {}", e.getMessage());
                        throw new AppException(ErrorCode.SERVICE_UNAVAILABLE);
                  }
            }

            List<String> imageUrls = handleImageUpload(images);
            Review review = reviewMapper.toReview(request);
            review.setUsername(username);
            review.setImageUrls(imageUrls);

            return reviewMapper.toReviewResponse(reviewRepository.save(review));
      }

      @Override
      public void respondToReview(String reviewId, String response) {
            String currentUsername = getCurrentUsername();

            ApiResponse<ShopResponse> shopResponse = shopClient.getShopByOwnerUsername(currentUsername);
            if (shopResponse == null || shopResponse.getResult() == null) {
                  log.error("Shop for user {} not found", currentUsername);
                  throw new AppException(ErrorCode.UNAUTHORIZED);
            }

            Review review = findReviewById(reviewId);
            String productId = review.getProductId();

            try {
                  ApiResponse<ProductResponse> productResponse = productClient.getProductById(productId);
                  if (productResponse == null || productResponse.getResult() == null) {
                        log.error("Product with ID {} not found", productId);
                        throw new AppException(ErrorCode.PRODUCT_NOT_FOUND);
                  }

                  String shopId = shopResponse.getResult().getId();
                  String productShopId = productResponse.getResult().getShopId();

                  if (!shopId.equals(productShopId)) {
                        log.error("Product with ID {} does not belong to the shop of user {}", productId, currentUsername);
                        throw new AppException(ErrorCode.UNAUTHORIZED);
                  }
            } catch (FeignException e) {
                  log.error("Error fetching product information for product ID {}: {}", productId, e.getMessage());
                  throw new AppException(ErrorCode.SERVICE_UNAVAILABLE);
            }

            review.setShopResponse(response);
            reviewRepository.save(review);
      }

      @Override
      public ReviewResponse updateReview(String reviewId, ReviewUpdateRequest request, List<MultipartFile> newImages) {
            String username = getCurrentUsername();
            Review review = findReviewById(reviewId);
            validateUserOwnership(review, username);

            List<String> uploadedImages = handleImageUpload(newImages);
            if (!uploadedImages.isEmpty()) {
                  List<String> allImages = review.getImageUrls();
                  allImages.addAll(uploadedImages);
                  review.setImageUrls(allImages);
            }

            reviewMapper.updateReview(review, request);
            return reviewMapper.toReviewResponse(reviewRepository.save(review));
      }

      @Override
      public void deleteReview(String reviewId) {
            validateUserOwnership(findReviewById(reviewId), getCurrentUsername());
            reviewRepository.deleteById(reviewId);
      }

      @Override
      public void deleteAllReviewsOfUser(String username) {
            reviewRepository.deleteAll(reviewRepository.findByUsername(username));
      }

      @Override
      public void deleteAllReviewsOfProduct(String productId) {
            reviewRepository.deleteAll(reviewRepository.findByProductId(productId));
      }

      @Override
      public double calculateAverageRating(String productId) {
            List<Review> reviews = reviewRepository.findByProductId(productId);
            if (reviews.isEmpty()) {
                  return 0;
            }

            double sum = reviews.stream().mapToInt(Review::getRating).sum();
            return sum / reviews.size();
      }

      @Override
      public ReviewResponse getReviewById(String reviewId) {
            return reviewMapper.toReviewResponse(findReviewById(reviewId));
      }

      @Override
      public Page<ReviewResponse> getReviewsByProductAndRating(String productId, int rating, int page, int size) {
            Pageable pageable = PageRequest.of(page, size);
            Page<Review> reviewPage = reviewRepository.findByProductIdAndRating(productId, rating, pageable);

            return reviewPage.map(reviewMapper::toReviewResponse);
      }

      @Override
      @PreAuthorize("hasRole('ADMIN')")
      public List<ReviewResponse> getAllReviews() {
            return reviewRepository.findAll()
                    .stream()
                    .map(reviewMapper::toReviewResponse)
                    .toList();
      }

      @Override
      public List<ReviewResponse> getReviewsByProductId(String productId) {
            return reviewRepository.findByProductId(productId)
                    .stream()
                    .map(reviewMapper::toReviewResponse)
                    .toList();
      }

      @Override
      @PreAuthorize("hasRole('ADMIN')")
      public List<ReviewResponse> getReviewsByUsername(String username) {
            return reviewRepository.findByUsername(username)
                    .stream()
                    .map(reviewMapper::toReviewResponse)
                    .toList();
      }

      private String getCurrentUsername() {
            Jwt jwt = (Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            return jwt.getClaim("preferred_username");
      }

      private Review findReviewById(String id) {
            return reviewRepository.findById(id)
                    .orElseThrow(() -> {
                          log.error("Review with ID {} not found", id);
                          return new AppException(ErrorCode.REVIEW_NOT_FOUND);
                    });
      }

      private void validateUserOwnership(Review review, String username) {
            if (!review.getUsername().equals(username)) {
                  log.error("User {} is not authorized to access the post owned by {}.", username, review.getUsername());
                  throw new AppException(ErrorCode.UNAUTHENTICATED);
            }
      }

      private List<String> handleImageUpload(List<MultipartFile> images) {
            if (images == null || images.isEmpty()) {
                  return List.of();
            }

            ApiResponse<List<FileResponse>> fileResponseApi;
            try {
                  fileResponseApi = fileClient.uploadMultipleFiles(images);

                  if (fileResponseApi == null || fileResponseApi.getResult() == null) {
                        log.error("File upload returned no results");
                        throw new AppException(ErrorCode.FILE_UPLOAD_FAILED);
                  }
            } catch (FeignException e) {
                  log.error("Error uploading files: {}", e.getMessage(), e);
                  throw new AppException(ErrorCode.SERVICE_UNAVAILABLE);
            }

            List<FileResponse> fileResponses = fileResponseApi.getResult();

            return fileResponses.stream()
                    .map(FileResponse::getUrl)
                    .collect(Collectors.toList());
      }

}