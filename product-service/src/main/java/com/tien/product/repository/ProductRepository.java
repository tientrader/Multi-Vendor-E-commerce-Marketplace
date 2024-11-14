package com.tien.product.repository;

import com.tien.product.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends MongoRepository<Product, String> {

      @NonNull
      Page<Product> findAll(@NonNull Pageable pageable);

      List<Product> findByShopId(String shopId);

}