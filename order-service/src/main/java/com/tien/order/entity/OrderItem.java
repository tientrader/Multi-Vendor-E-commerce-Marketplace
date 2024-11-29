package com.tien.order.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "order_items",
        indexes = {
                @Index(name = "idx_order_items_product_variant", columnList = "productId, variantId")
        })
public class OrderItem {

      @Id
      @GeneratedValue(strategy = GenerationType.IDENTITY)
      Long orderItemId;

      @Column(nullable = false)
      String productId;

      @Column(nullable = false)
      String variantId;

      @Column(nullable = false)
      int quantity;

}