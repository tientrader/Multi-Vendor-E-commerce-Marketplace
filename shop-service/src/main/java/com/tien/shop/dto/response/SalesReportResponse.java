package com.tien.shop.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SalesReportResponse {

      double totalRevenue;
      int totalItemsSold;
      List<Map.Entry<String, Integer>> sortedProductSales;
      String topSellingProduct;
      double highestRevenueProductRevenue;
      String highestRevenueProduct;
      String startDate;
      String endDate;

}