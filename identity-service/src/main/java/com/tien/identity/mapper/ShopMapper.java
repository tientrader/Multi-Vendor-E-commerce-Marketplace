package com.tien.identity.mapper;

import com.tien.identity.dto.request.ShopCreationRequest;
import com.tien.identity.dto.response.ShopResponse;
import com.tien.identity.entity.Shop;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ShopMapper {

      Shop toShop(ShopCreationRequest request);

      ShopResponse toShopResponse(Shop shop);

}