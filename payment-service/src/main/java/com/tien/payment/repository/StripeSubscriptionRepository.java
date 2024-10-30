package com.tien.payment.repository;

import com.tien.payment.entity.StripeSubscription;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface StripeSubscriptionRepository extends MongoRepository<StripeSubscription, String> {

      StripeSubscription findByStripeSubscriptionId(String stripeSubscriptionId);

}