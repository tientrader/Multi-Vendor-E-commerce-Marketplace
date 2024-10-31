package com.tien.payment.repository;

import com.tien.payment.entity.StripeCharge;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StripeChargeRepository extends JpaRepository<StripeCharge, String> {}