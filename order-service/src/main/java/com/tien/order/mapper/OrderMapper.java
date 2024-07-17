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
              @Mapping(target = "orderId", ignore = true),
              @Mapping(source = "request.userId", target = "userId"),
              @Mapping(source = "items", target = "items"),
              @Mapping(source = "total", target = "total"),
              @Mapping(source = "request.status", target = "status")
      })
      Order toOrder(OrderCreationRequest request, List<OrderItem> items, double total);

      @Mappings({
              @Mapping(source = "order.orderId", target = "orderId"),
              @Mapping(source = "order.userId", target = "userId"),
              @Mapping(source = "order.status", target = "status"),
              @Mapping(target = "items", ignore = true),
              @Mapping(target = "userProfile", ignore = true)
      })
      OrderResponse toOrderResponse(Order order);

      @Mapping(target = "orderItemId", ignore = true)
      OrderItem toOrderItem(OrderItemCreationRequest request);

      @Mapping(source = "productResponse", target = "productDetails")
      OrderItemResponse toOrderItemResponse(OrderItem orderItem, ProductResponse productResponse);

      // Product mappings
      @Mapping(source = "id", target = "productId")
      OrderItemCreationRequest toOrderItemCreationRequest(ProductResponse productResponse);

      // UserProfile mappings
      @Mapping(source = "userId", target = "userId")
      OrderCreationRequest toOrderCreationRequest(UserProfileResponse userProfileResponse);

}