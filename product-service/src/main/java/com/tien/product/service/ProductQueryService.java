package com.tien.product.service;

import com.tien.product.dto.response.ExistsResponse;
import com.tien.product.dto.response.ProductResponse;
import com.tien.product.service.impl.ProductSort;
import org.springframework.data.domain.Page;

import java.util.List;

public interface ProductQueryService {

      ProductResponse getProductById(String productId);
      double getProductPriceById(String productId);
      ExistsResponse existsProduct(String productId);
      List<ProductResponse> getAllProducts();
      Page<ProductResponse> getProducts(String shopId, String categoryId, int page, int size,
                                        String sortBy, String sortDirection, Double minPrice,
                                        Double maxPrice, ProductSort productSort);

}