package com.tien.identity.mapper;

import com.tien.identity.dto.request.ShopCreationRequest;
import com.tien.identity.dto.request.ShopUpdateRequest;
import com.tien.identity.dto.response.ShopResponse;
import com.tien.identity.entity.Shop;
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