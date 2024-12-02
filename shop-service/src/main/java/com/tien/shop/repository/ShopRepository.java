package com.tien.shop.repository;

import com.tien.shop.entity.Shop;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ShopRepository extends MongoRepository<Shop, String> {

      @Query("{ 'name' : { $regex: ?0, $options: 'i' } }")
      Page<Shop> searchShops(@Param("keyword") String keyword, Pageable pageable);

      boolean existsByOwnerUsername(String ownerUsername);

      Optional<Shop> findByOwnerUsername(String ownerUsername);

}