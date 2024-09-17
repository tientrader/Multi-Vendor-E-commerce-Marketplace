package com.tien.shop.service;

import com.tien.shop.dto.request.ShopCreationRequest;
import com.tien.shop.dto.request.ShopUpdateRequest;
import com.tien.shop.dto.response.ShopResponse;

public interface ShopService {

      ShopResponse createShop(ShopCreationRequest request);
      ShopResponse getShopByOwnerUsername(String ownerUsername);
      ShopResponse updateShop(ShopUpdateRequest request);
      void deleteShop();

}