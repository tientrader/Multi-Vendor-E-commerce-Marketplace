package com.tien.order.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "orders")
public class Order {

      @Id
      @GeneratedValue(strategy = GenerationType.IDENTITY)
      Long id;

      @Column(nullable = false)
      String userId;

      @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
      @JoinColumn(name = "order_id")
      List<OrderItem> items;

      @Column(nullable = false)
      double total;

      @Column(nullable = false)
      String status;

}