package com.tien.product.service;

import com.tien.product.dto.request.ProductCreationRequest;
import com.tien.product.dto.request.ProductUpdateRequest;
import com.tien.product.dto.response.ExistsResponse;
import com.tien.product.dto.response.ProductResponse;
import org.springframework.data.domain.Page;

import java.util.List;

public interface ProductService {

      ProductResponse createProduct(ProductCreationRequest request);

//      Page<ProductResponse> getHomepageProductList(
//              int page, int size, String sortBy, String sortDirection,
//              String categoryId, Double minPrice, Double maxPrice);
//
//      Page<ProductResponse> getProductsByShop(
//              String shopId, int page, int size, String sortBy, String sortDirection,
//              String categoryId, Double minPrice, Double maxPrice);
//
//      Page<ProductResponse> getProductsByCategoryId(
//              String category, int page, int size, String sortBy, String sortDirection,
//              Double minPrice, Double maxPrice);

      Page<ProductResponse> getProducts(
              String shopId, String categoryId,
              int page, int size, String sortBy, String sortDirection,
              Double minPrice, Double maxPrice);

      List<ProductResponse> getAllProducts();
      ProductResponse getProductById(String productId);
      double getProductPriceById(String productId);
      ExistsResponse existsProduct(String productId);
      ProductResponse updateProduct(String productId, ProductUpdateRequest request);
      void updateStockAndSoldQuantity(String productId, int quantity);
      void deleteProduct(String productId);

}