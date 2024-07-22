package com.tien.identity.entity;

import jakarta.persistence.*;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
public class Shop {

      @Id
      @GeneratedValue(strategy = GenerationType.IDENTITY)
      Long id;

      String name;
      String email;
      String ownerUsername;

}