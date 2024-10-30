package com.tien.payment.entity;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "session")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Session {

      @Id
      String sessionId;
      String sessionUrl;
      String username;

}