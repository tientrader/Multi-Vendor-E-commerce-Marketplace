package com.tien.payment.repository;

import com.tien.payment.entity.PayPal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PayPalRepository extends JpaRepository<PayPal, Long> {}