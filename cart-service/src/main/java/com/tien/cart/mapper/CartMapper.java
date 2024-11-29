package com.tien.cart.mapper;

import com.tien.cart.dto.request.CartCreationRequest;
import com.tien.cart.httpclient.request.OrderCreationRequest;
import com.tien.cart.httpclient.request.OrderItemCreationRequest;
import com.tien.cart.dto.response.CartItemResponse;
import com.tien.cart.dto.response.CartResponse;
import com.tien.cart.entity.Cart;
import com.tien.cart.entity.CartItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CartMapper {

      Cart toCart(CartCreationRequest cartCreationRequest);

      @Mapping(source = "id", target = "cartId")
      CartResponse toCartResponse(Cart cart);

      OrderCreationRequest toOrderCreationRequest(Cart cart);

      List<OrderItemCreationRequest> mapCartItemsToOrderItems(List<CartItem> cartItems);

      CartItemResponse toCartItemResponse(CartItem cartItem);

}