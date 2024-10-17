package com.tien.payment.repository;

import com.tien.payment.entity.Session;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface SessionRepository extends MongoRepository<Session, String> {}