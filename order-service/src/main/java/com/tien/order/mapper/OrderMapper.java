package com.tien.order.mapper;

import com.tien.order.dto.request.OrderCreationRequest;
import com.tien.order.dto.response.OrderItemResponse;
import com.tien.order.dto.response.OrderResponse;
import com.tien.order.entity.Order;
import com.tien.order.entity.OrderItem;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface OrderMapper {

      @Mapping(target = "orderId", ignore = true)
      Order toOrder(OrderCreationRequest request);

      OrderItemResponse toOrderItemResponse(OrderItem orderItem);

      List<OrderItemResponse> toOrderItemResponses(List<OrderItem> orderItems);

      OrderResponse toOrderResponse(Order order);

}