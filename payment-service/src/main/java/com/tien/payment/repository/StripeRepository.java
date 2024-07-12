package com.tien.payment.repository;

import com.tien.payment.entity.Stripe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StripeRepository extends JpaRepository<Stripe, Long> {

      Stripe findByChargeId(String chargeId);

}