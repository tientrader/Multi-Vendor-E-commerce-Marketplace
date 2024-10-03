package com.tien.product.service.impl;

import com.tien.product.dto.response.ExistsResponse;
import com.tien.product.dto.response.ProductResponse;
import com.tien.product.entity.Product;
import com.tien.product.exception.AppException;
import com.tien.product.exception.ErrorCode;
import com.tien.product.mapper.ProductMapper;
import com.tien.product.repository.ProductRepository;
import com.tien.product.service.ProductQueryService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ProductQueryServiceImpl implements ProductQueryService {

      ProductRepository productRepository;
      ProductMapper productMapper;
      MongoTemplate mongoTemplate;

      @Override
      public Page<ProductResponse> getProducts(
              String shopId, String categoryId,
              int page, int size, String sortBy, String sortDirection,
              Double minPrice, Double maxPrice, ProductSort productSort) {

            productSort = Optional.ofNullable(productSort).orElse(ProductSort.DEFAULT);

            Criteria criteria = buildCriteriaAndPrice(shopId, categoryId, minPrice, maxPrice);
            Sort sort = determineSortOrder(productSort, sortBy, sortDirection);

            return getProductsPage(criteria, page, size, sort);
      }

      @Override
      public List<ProductResponse> getAllProducts() {
            log.info("Fetching all products");
            return productRepository.findAll().stream()
                    .map(productMapper::toProductResponse)
                    .collect(Collectors.toList());
      }

      @Override
      public ProductResponse getProductById(String productId) {
            log.info("Fetching product with ID: {}", productId);
            return productMapper.toProductResponse(productRepository.findById(productId)
                    .orElseThrow(() -> {
                          log.error("(getProductById) Product not found with ID: {}", productId);
                          return new AppException(ErrorCode.PRODUCT_NOT_FOUND);
                    }));
      }

      @Override
      public double getProductPriceById(String productId) {
            log.info("Fetching price for product with ID: {}", productId);
            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> {
                          log.error("(getProductPriceById) Product not found with ID: {}", productId);
                          return new AppException(ErrorCode.PRODUCT_NOT_FOUND);
                    });
            return product.getPrice();
      }

      @Override
      public ExistsResponse existsProduct(String productId) {
            log.info("Checking existence for product with ID: {}", productId);
            boolean exists = productRepository.existsById(productId);
            return new ExistsResponse(exists);
      }

      private Criteria buildCriteriaAndPrice(String shopId, String categoryId, Double minPrice, Double maxPrice) {
            Criteria criteria = new Criteria();

            if (shopId != null) {
                  criteria.and("shopId").is(shopId);
                  log.info("Fetching products for shopId: {}", shopId);
            } else if (categoryId != null) {
                  criteria.and("category.id").is(categoryId);
                  log.info("Fetching products for categoryId: {}", categoryId);
            } else {
                  log.info("Fetching products for homepage");
            }

            if (minPrice != null && maxPrice != null) {
                  criteria.and("price").gte(minPrice).lte(maxPrice);
            } else if (minPrice != null) {
                  criteria.and("price").gte(minPrice);
            } else if (maxPrice != null) {
                  criteria.and("price").lte(maxPrice);
            }

            return criteria;
      }

      private Sort determineSortOrder(ProductSort productSort, String sortBy, String sortDirection) {
            return switch (productSort) {
                  case BEST_SELLING -> Sort.by(Sort.Direction.DESC, "soldQuantity");
                  case NEWEST -> Sort.by(Sort.Direction.DESC, "createdAt");
                  default -> Sort.by(Sort.Direction.fromString(sortDirection), sortBy);
            };
      }

      private Page<ProductResponse> getProductsPage(Criteria criteria, int page, int size, Sort sort) {
            Query query = Query.query(criteria).with(PageRequest.of(page, size, sort));

            List<Product> products = mongoTemplate.find(query, Product.class);
            long total = mongoTemplate.count(query, Product.class);
            List<ProductResponse> productResponses = productMapper.toProductResponses(products);

            return new PageImpl<>(productResponses, PageRequest.of(page, size, sort), total);
      }

}