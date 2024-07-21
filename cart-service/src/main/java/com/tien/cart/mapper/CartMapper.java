package com.tien.cart.mapper;

import com.tien.cart.dto.request.CartCreationRequest;
import com.tien.cart.dto.request.OrderCreationRequest;
import com.tien.cart.dto.request.OrderItemCreationRequest;
import com.tien.cart.dto.response.CartResponse;
import com.tien.cart.entity.Cart;
import com.tien.cart.entity.ProductInCart;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring", uses = ProductInCartMapper.class)
public interface CartMapper {

      @Mapping(target = "total", ignore = true)
      @Mapping(target = "id", ignore = true)
      Cart toCart(CartCreationRequest cartCreationRequest);

      @Mapping(source = "id", target = "cartId")
      CartResponse toCartResponse(Cart cart);

      @Mapping(source = "userId", target = "userId")
      @Mapping(source = "total", target = "total")
      @Mapping(source = "productInCarts", target = "items")
      OrderCreationRequest toOrderCreationRequest(Cart cart);

      default List<OrderItemCreationRequest> mapProductInCartsToOrderItemRequests(List<ProductInCart> productInCarts) {
            return productInCarts.stream()
                    .map(productInCart -> OrderItemCreationRequest.builder()
                            .productId(productInCart.getProductId())
                            .quantity(productInCart.getQuantity())
                            .build())
                    .toList();
      }

}