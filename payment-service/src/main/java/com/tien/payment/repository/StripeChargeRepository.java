package com.tien.payment.repository;

import com.tien.payment.entity.StripeCharge;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface StripeChargeRepository extends MongoRepository<StripeCharge, String> {}