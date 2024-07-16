package com.tien.order.mapper;

import com.tien.order.dto.request.OrderCreationRequest;
import com.tien.order.dto.request.OrderItemCreationRequest;
import com.tien.order.dto.response.OrderItemResponse;
import com.tien.order.dto.response.OrderResponse;
import com.tien.order.dto.response.ProductResponse;
import com.tien.order.dto.response.UserProfileResponse;
import com.tien.order.entity.Order;
import com.tien.order.entity.OrderItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import java.util.List;

@Mapper(componentModel = "spring")
public interface OrderMapper {

      @Mappings({
              @Mapping(target = "id", ignore = true),
              @Mapping(target = "userId", source = "orderCreationRequest.userId"),
              @Mapping(target = "items", source = "items"),
              @Mapping(target = "total", ignore = true),
              @Mapping(target = "status", constant = "CREATED")
      })
      Order toOrder(OrderCreationRequest orderCreationRequest, List<OrderItem> items);

      List<OrderItem> toOrderItems(List<OrderItemCreationRequest> orderItemCreationRequests);

      List<OrderItemResponse> toOrderItemResponses(List<OrderItem> orderItems);

      @Mapping(target = "userProfile", ignore = true)
      @Mapping(target = "total", ignore = true)
      @Mapping(target = "id", source = "order.id")
      @Mapping(target = "userId", source = "order.userId")
      OrderResponse toOrderResponse(Order order, List<OrderItemResponse> orderItemResponses, UserProfileResponse userProfileResponse);

      @Mapping(target = "id", source = "orderItem.id")
      @Mapping(target = "productDetails", ignore = true)
      @Mapping(target = "productId", source = "orderItem.productId")
      @Mapping(target = "quantity", source = "orderItem.quantity")
      OrderItemResponse toOrderItemResponse(OrderItem orderItem, ProductResponse productResponse);

}