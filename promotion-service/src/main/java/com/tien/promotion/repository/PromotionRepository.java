package com.tien.promotion.repository;

import com.tien.promotion.entity.Promotion;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PromotionRepository extends MongoRepository<Promotion, String> {

      Optional<Promotion> findByPromoCode(String promoCode);

}