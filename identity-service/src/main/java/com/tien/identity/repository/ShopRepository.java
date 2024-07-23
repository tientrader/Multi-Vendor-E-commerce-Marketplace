package com.tien.identity.repository;

import com.tien.identity.entity.Shop;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ShopRepository extends JpaRepository<Shop, Long> {

      boolean existsByOwnerUsername(String ownerUsername);

      Optional<Shop> findByOwnerUsername(String ownerUsername);

}