package com.tien.cart.mapper;

import com.tien.cart.dto.request.CartCreationRequest;
import com.tien.cart.dto.response.CartResponse;
import com.tien.cart.entity.Cart;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CartMapper {

      @Mapping(target = "id", ignore = true)
      Cart toCart(CartCreationRequest cartRequest);

      @Mapping(target = "status", ignore = true)
      @Mapping(target = "cartId", ignore = true)
      CartResponse toCartResponse(Cart cart);

}