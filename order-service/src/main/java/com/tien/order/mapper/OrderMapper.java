package com.tien.order.mapper;

import com.tien.order.dto.request.OrderCreationRequest;
import com.tien.order.dto.response.OrderItemResponse;
import com.tien.order.dto.response.OrderResponse;
import com.tien.order.entity.Order;
import com.tien.order.entity.OrderItem;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import java.util.List;

@Mapper(componentModel = "spring")
public interface OrderMapper {

      @Mappings({
              @Mapping(source = "items", target = "items"),
              @Mapping(source = "userId", target = "userId"),
              @Mapping(source = "total", target = "total"),
              @Mapping(source = "status", target = "status"),
              @Mapping(target = "orderId", ignore = true)
      })
      Order toOrder(OrderCreationRequest request);

      @Mappings({
              @Mapping(source = "productId", target = "productId"),
              @Mapping(source = "quantity", target = "quantity")
      })
      OrderItemResponse toOrderItemResponse(OrderItem orderItem);

      List<OrderItemResponse> toOrderItemResponses(List<OrderItem> orderItems);

      @Mappings({
              @Mapping(source = "orderId", target = "orderId"),
              @Mapping(source = "userId", target = "userId"),
              @Mapping(source = "total", target = "total"),
              @Mapping(source = "status", target = "status"),
              @Mapping(source = "items", target = "items")
      })
      OrderResponse toOrderResponse(Order order);

}