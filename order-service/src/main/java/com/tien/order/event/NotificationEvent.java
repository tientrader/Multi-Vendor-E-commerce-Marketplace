package com.tien.order.event;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NotificationEvent {

      String channel;
      String recipient;
      String templateCode;
      Map<String, Object> param;
      String subject;
      String body;

}