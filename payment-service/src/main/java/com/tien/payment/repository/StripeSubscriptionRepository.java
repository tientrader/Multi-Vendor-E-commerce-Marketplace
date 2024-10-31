package com.tien.payment.repository;

import com.tien.payment.entity.StripeSubscription;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StripeSubscriptionRepository extends JpaRepository<StripeSubscription, String> {}