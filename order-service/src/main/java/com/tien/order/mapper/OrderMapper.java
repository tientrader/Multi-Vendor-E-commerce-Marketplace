package com.tien.order.mapper;

import com.tien.order.dto.request.OrderCreationRequest;
import com.tien.order.dto.response.OrderResponse;
import com.tien.order.entity.Order;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OrderMapper {

      @Mapping(target = "total", ignore = true)
      @Mapping(target = "status", ignore = true)
      @Mapping(target = "id", ignore = true)
      @Mapping(target = "items", source = "items")
      Order toOrder(OrderCreationRequest orderCreationRequest);

      @Mapping(target = "items", source = "items")
      OrderResponse toOrderResponse(Order order);

}