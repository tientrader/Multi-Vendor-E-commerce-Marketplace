package com.tien.product.service;

import com.tien.product.dto.request.ProductUpdateRequest;
import com.tien.product.dto.response.ExistsResponse;
import com.tien.product.entity.Category;
import com.tien.product.mapper.ProductMapper;
import com.tien.product.repository.CategoryRepository;
import com.tien.product.repository.ProductRepository;
import com.tien.product.dto.request.ProductCreationRequest;
import com.tien.product.dto.response.ProductResponse;
import com.tien.product.entity.Product;
import com.tien.product.exception.AppException;
import com.tien.product.exception.ErrorCode;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

import org.springframework.data.domain.*;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ProductService {

      ProductRepository productRepository;
      ProductMapper productMapper;
      CategoryRepository categoryRepository;
      MongoTemplate mongoTemplate;

      // Add product
      @PreAuthorize("hasRole('ADMIN')")
      @Transactional
      public ProductResponse createProduct(ProductCreationRequest request) {
            Product product = productMapper.toProduct(request);

            Category category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_FOUND));

            product.setCategory(category);
            product = productRepository.save(product);

            category.getProducts().add(product);
            categoryRepository.save(category);

            return productMapper.toProductResponse(product);
      }

      // Update product by productId
      @PreAuthorize("hasRole('ADMIN')")
      @Transactional
      public ProductResponse updateProduct(String productId, ProductUpdateRequest request) {
            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));

            Category newCategory = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_FOUND));

            Category oldCategory = product.getCategory();
            oldCategory.getProducts().remove(product);
            categoryRepository.save(oldCategory);

            productMapper.updateProduct(product, request);
            product.setCategory(newCategory);
            product = productRepository.save(product);

            newCategory.getProducts().add(product);
            categoryRepository.save(newCategory);

            return productMapper.toProductResponse(product);
      }

      @Transactional
      public void updateStock(String productId, int quantity) {
            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));

            int newStock = product.getStock() + quantity;
            if (newStock < 0) throw new AppException(ErrorCode.OUT_OF_STOCK);

            product.setStock(newStock);
            productRepository.save(product);
      }

      // Delete product by productId
      @PreAuthorize("hasRole('ADMIN')")
      @Transactional
      public void deleteProduct(String productId) {
            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));

            Category category = product.getCategory();
            category.getProducts().remove(product);
            categoryRepository.save(category);

            productRepository.deleteById(productId);
      }

      // Method for paginated, sorted, and filtered product retrieval
      public Page<ProductResponse> getFilteredSortedPaginatedProducts(
              int page, int size, String sortBy, String sortDirection,
              String categoryId, Double minPrice, Double maxPrice) {

            Criteria criteria = new Criteria();

            if (categoryId != null) criteria.and("categoryId").is(categoryId);

            if (minPrice != null && maxPrice != null) {
                  criteria.and("price").gte(minPrice).lte(maxPrice);
            } else if (minPrice != null) {
                  criteria.and("price").gte(minPrice);
            } else if (maxPrice != null) {
                  criteria.and("price").lte(maxPrice);
            }

            Query query = Query.query(criteria);
            Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(sortDirection), sortBy));
            query.with(pageable);

            List<Product> products = mongoTemplate.find(query, Product.class);
            long total = mongoTemplate.count(query, Product.class);
            List<ProductResponse> productResponses = productMapper.toProductResponses(products);
            return new PageImpl<>(productResponses, pageable, total);
      }

      // Display all products
      public List<ProductResponse> getAllProducts() {
            return productRepository.findAll().stream()
                    .map(productMapper::toProductResponse)
                    .collect(Collectors.toList());
      }

      // Display product by productId
      public ProductResponse getProductById(String productId) {
            return productMapper.toProductResponse(productRepository.findById(productId)
                    .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND)));
      }

      // Display product price by productId
      public double getProductPriceById(String productId) {
            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));
            return product.getPrice();
      }

      // Check if product exists by productId
      public ExistsResponse existsProduct(String productId) {
            boolean exists = productRepository.existsById(productId);
            return new ExistsResponse(exists);
      }

}