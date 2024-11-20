package com.tien.shop.service;

import com.tien.shop.dto.request.ShopCreationRequest;
import com.tien.shop.dto.request.ShopUpdateRequest;
import com.tien.shop.dto.response.SalesReportResponse;
import com.tien.shop.dto.response.ShopResponse;

public interface ShopService {

      ShopResponse createShop(ShopCreationRequest request);

      ShopResponse updateShop(ShopUpdateRequest request);

      void deleteShop();

      SalesReportResponse generateSalesReport(String shopId, String startDate, String endDate);

      SalesReportResponse getMySalesReport(String startDate, String endDate);

      ShopResponse getShopByOwnerUsername(String ownerUsername);

      String getOwnerUsernameByShopId(String shopId);

}