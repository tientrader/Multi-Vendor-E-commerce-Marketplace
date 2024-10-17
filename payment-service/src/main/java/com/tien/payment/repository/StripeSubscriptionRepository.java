package com.tien.payment.repository;

import com.tien.payment.entity.StripeSubscription;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface StripeSubscriptionRepository extends MongoRepository<StripeSubscription, String> {

      Optional<StripeSubscription> findByStripeSubscriptionId(String stripeSubscriptionId);

}