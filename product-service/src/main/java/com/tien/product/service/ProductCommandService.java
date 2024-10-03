package com.tien.product.service;

import com.tien.product.dto.request.ProductCreationRequest;
import com.tien.product.dto.request.ProductUpdateRequest;
import com.tien.product.dto.response.ProductResponse;

public interface ProductCommandService {

      ProductResponse createProduct(ProductCreationRequest request);
      ProductResponse updateProduct(String productId, ProductUpdateRequest request);
      void updateStockAndSoldQuantity(String productId, int quantity);
      void deleteProduct(String productId);

}