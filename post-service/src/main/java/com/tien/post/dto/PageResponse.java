package com.tien.post.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Collections;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PageResponse <T> {

      int currentPage;
      int totalPages;
      int pageSize;
      long totalElements;

      @Builder.Default
      List<T> data = Collections.emptyList();

}