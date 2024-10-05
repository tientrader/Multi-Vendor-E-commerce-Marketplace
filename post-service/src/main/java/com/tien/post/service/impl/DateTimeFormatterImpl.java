package com.tien.post.service.impl;

import com.tien.post.service.DateTimeFormatterService;
import org.springframework.stereotype.Component;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class DateTimeFormatterImpl implements DateTimeFormatterService {

      private static final java.time.format.DateTimeFormatter DATE_FORMATTER = java.time.format.DateTimeFormatter.ISO_DATE;

      private static final Map<Long, Function<Long, String>> STRATEGY_MAP = new LinkedHashMap<>();

      static {
            STRATEGY_MAP.put(60L, elapsed -> elapsed + " seconds ago");
            STRATEGY_MAP.put(3600L, elapsed -> (elapsed / 60) + " minutes ago");
            STRATEGY_MAP.put(86400L, elapsed -> (elapsed / 3600) + " hours ago");
            STRATEGY_MAP.put(Long.MAX_VALUE, elapsed -> LocalDateTime.now().minusSeconds(elapsed).format(DATE_FORMATTER));
      }

      @Override
      public String format(Instant instant) {
            long elapsedSeconds = ChronoUnit.SECONDS.between(instant, Instant.now());

            return STRATEGY_MAP.entrySet().stream()
                    .filter(entry -> elapsedSeconds < entry.getKey())
                    .findFirst()
                    .map(entry -> entry.getValue().apply(elapsedSeconds))
                    .orElseThrow();
      }

}