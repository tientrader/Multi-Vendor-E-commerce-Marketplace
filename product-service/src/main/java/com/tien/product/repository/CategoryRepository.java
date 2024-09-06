package com.tien.product.repository;

import com.tien.product.entity.Category;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends MongoRepository<Category, String> {

      List<Category> findByShopId(String shopId);

}