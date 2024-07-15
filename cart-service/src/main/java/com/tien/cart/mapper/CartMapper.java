package com.tien.cart.mapper;

import com.tien.cart.dto.request.CartCreationRequest;
import com.tien.cart.dto.response.CartResponse;
import com.tien.cart.entity.Cart;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CartMapper {

      @Mapping(target = "id", ignore = true)
      @Mapping(target = "products", source = "cartCreationRequest.products")
      Cart toCart(CartCreationRequest cartCreationRequest);

      @Mapping(source = "id", target = "cartId")
      CartResponse toCartResponse(Cart cart);

}