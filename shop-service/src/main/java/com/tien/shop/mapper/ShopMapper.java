package com.tien.shop.mapper;

import com.tien.shop.dto.request.ShopCreationRequest;
import com.tien.shop.dto.request.ShopUpdateRequest;
import com.tien.shop.dto.response.ShopResponse;
import com.tien.shop.entity.Shop;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ShopMapper {

      @Mapping(target = "ownerUsername", ignore = true)
      @Mapping(target = "id", ignore = true)
      Shop toShop(ShopCreationRequest request);

      ShopResponse toShopResponse(Shop shop);

      @Mapping(target = "ownerUsername", ignore = true)
      @Mapping(target = "id", ignore = true)
      void updateShop(@MappingTarget Shop shop, ShopUpdateRequest request);

}