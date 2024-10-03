package com.tien.product.service;

import com.tien.product.dto.response.ExistsResponse;
import com.tien.product.dto.response.ProductResponse;
import com.tien.product.service.impl.ProductSort;
import org.springframework.data.domain.Page;

import java.util.List;

public interface ProductQueryService {

      Page<ProductResponse> getProducts(String shopId, String categoryId, int page, int size,
                                        String sortBy, String sortDirection, Double minPrice,
                                        Double maxPrice, ProductSort productSort);
      ProductResponse getProductById(String productId);
      List<ProductResponse> getAllProducts();
      double getProductPriceById(String productId, String variantId);
      ExistsResponse existsProduct(String productId, String variantId);

}