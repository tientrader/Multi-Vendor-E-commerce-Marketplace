package com.tien.cart.mapper;

import com.tien.cart.dto.request.CartCreationRequest;
import com.tien.cart.dto.request.OrderCreationRequest;
import com.tien.cart.dto.response.CartResponse;
import com.tien.cart.entity.Cart;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CartMapper {

      @Mapping(target = "total", ignore = true)
      @Mapping(target = "id", ignore = true)
      Cart toCart(CartCreationRequest cartCreationRequest);

      @Mapping(source = "id", target = "cartId")
      CartResponse toCartResponse(Cart cart);

      @Mapping(target = "status", ignore = true)
      @Mapping(source = "username", target = "username")
      @Mapping(source = "total", target = "total")
      @Mapping(source = "items", target = "items")
      OrderCreationRequest toOrderCreationRequest(Cart cart);

}