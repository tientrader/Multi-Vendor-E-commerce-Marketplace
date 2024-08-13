package com.tien.shop.repository;

import com.tien.shop.entity.Shop;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface ShopRepository extends MongoRepository<Shop, String> {

      boolean existsByOwnerUsername(String ownerUsername);

      Optional<Shop> findByOwnerUsername(String ownerUsername);

}