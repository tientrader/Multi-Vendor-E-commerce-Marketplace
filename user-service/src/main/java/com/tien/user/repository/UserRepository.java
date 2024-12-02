package com.tien.user.repository;

import com.tien.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String> {

      @Query("SELECT u FROM User u WHERE " +
              "LOWER(u.username) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
              "LOWER(u.email) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
              "LOWER(u.phoneNumber) LIKE LOWER(CONCAT('%', :keyword, '%'))")
      Page<User> searchUsers(@Param("keyword") String keyword, Pageable pageable);

      Optional<User> findByUserId(String userId);

      Optional<User> findByEmail(String email);

      Optional<User> findByUsername(String username);

      void deleteByUserId(String userId);

      boolean existsByPhoneNumber(String phoneNumber);

}