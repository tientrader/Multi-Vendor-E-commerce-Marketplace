package com.tien.user.repository;

import com.tien.user.entity.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends MongoRepository<User, String> {

      Optional<User> findByUserId(String userId);

      void deleteByUserId(String userId);

}