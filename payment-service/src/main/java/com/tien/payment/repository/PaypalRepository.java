package com.tien.payment.repository;

import com.tien.payment.entity.Paypal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaypalRepository extends JpaRepository<Paypal, Long> {}