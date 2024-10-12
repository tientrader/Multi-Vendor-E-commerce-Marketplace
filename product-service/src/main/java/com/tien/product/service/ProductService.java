package com.tien.product.service;

import com.tien.product.dto.request.ProductCreationRequest;
import com.tien.product.dto.request.ProductUpdateRequest;
import com.tien.product.dto.response.ExistsResponse;
import com.tien.product.dto.response.ProductResponse;
import com.tien.product.service.impl.ProductSort;
import org.springframework.data.domain.Page;

import java.util.List;

public interface ProductService {

      ProductResponse createProduct(ProductCreationRequest request);
      ProductResponse updateProduct(String productId, ProductUpdateRequest request);
      void updateStockAndSoldQuantity(String productId, String variantId, int quantity);
      void deleteProduct(String productId);
      Page<ProductResponse> getProducts(String shopId, String categoryId, int page, int size,
                                        String sortBy, String sortDirection, Double minPrice,
                                        Double maxPrice, ProductSort productSort);
      ProductResponse getProductById(String productId);
      List<ProductResponse> getAllProducts();
      double getProductPriceById(String productId, String variantId);
      ExistsResponse existsProduct(String productId, String variantId);

}