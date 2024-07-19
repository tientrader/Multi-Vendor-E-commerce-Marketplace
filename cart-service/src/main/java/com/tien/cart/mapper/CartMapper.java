package com.tien.cart.mapper;

import com.tien.cart.dto.request.CartCreationRequest;
import com.tien.cart.dto.response.CartResponse;
import com.tien.cart.dto.response.ProductResponse;
import com.tien.cart.entity.Cart;
import com.tien.cart.entity.ProductInCart;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CartMapper {

      @Mapping(target = "id", ignore = true)
      @Mapping(target = "products", source = "products")
      @Mapping(target = "total", ignore = true)
      Cart toCart(CartCreationRequest cartCreationRequest);

      @Mapping(source = "id", target = "cartId")
      @Mapping(source = "total", target = "total")
      CartResponse toCartResponse(Cart cart);

      ProductResponse toProductResponse(ProductInCart productInCart);

      ProductInCart toProductInCart(ProductResponse productResponse);

}