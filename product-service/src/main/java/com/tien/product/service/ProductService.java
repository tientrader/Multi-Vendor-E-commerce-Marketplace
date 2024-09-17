package com.tien.product.service;

import com.tien.product.dto.request.ProductCreationRequest;
import com.tien.product.dto.request.ProductUpdateRequest;
import com.tien.product.dto.response.ExistsResponse;
import com.tien.product.dto.response.ProductResponse;
import org.springframework.data.domain.Page;

import java.util.List;

public interface ProductService {

      ProductResponse createProduct(ProductCreationRequest request);
      Page<ProductResponse> searchProducts(int page, int size, String sortBy, String sortDirection,
                                           String categoryId, Double minPrice, Double maxPrice);
      List<ProductResponse> getAllProducts();
      ProductResponse getProductById(String productId);
      double getProductPriceById(String productId);
      ExistsResponse existsProduct(String productId);
      ProductResponse updateProduct(String productId, ProductUpdateRequest request);
      void updateStock(String productId, int quantity);
      void deleteProduct(String productId);

}