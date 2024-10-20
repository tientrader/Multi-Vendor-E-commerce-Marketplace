package com.tien.user.repository;

import com.tien.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String> {

      Optional<User> findByUserId(String userId);

      Optional<User> findByEmail(String email);

      Optional<User> findByUsername(String username);

      void deleteByUserId(String userId);

      boolean existsByUserId(String userId);

}