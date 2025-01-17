package com.tien.order.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "orders",
        indexes = {
                @Index(name = "idx_orders_username", columnList = "username"),
                @Index(name = "idx_orders_status", columnList = "status")
        })
public class Order {

      @Id
      @GeneratedValue(strategy = GenerationType.IDENTITY)
      Long orderId;

      @Column(nullable = false)
      String username;

      @Column(nullable = false)
      String email;

      @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
      @JoinColumn(name = "order_id")
      List<OrderItem> items;

      @Column(nullable = false)
      double total;

      @Column(nullable = false)
      String status;

      @Column(nullable = false)
      String paymentMethod;

      @CreatedDate
      LocalDateTime createdAt;

      @LastModifiedDate
      LocalDateTime updatedAt;

}