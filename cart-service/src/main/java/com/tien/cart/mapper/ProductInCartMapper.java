package com.tien.cart.mapper;

import com.tien.cart.dto.request.ProductInCartCreationRequest;
import com.tien.cart.dto.response.ProductInCartResponse;
import com.tien.cart.entity.ProductInCart;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProductInCartMapper {

      ProductInCart toProductInCart(ProductInCartCreationRequest productInCartCreationRequest);

      ProductInCartResponse toProductInCartResponse(ProductInCart productInCart);

}