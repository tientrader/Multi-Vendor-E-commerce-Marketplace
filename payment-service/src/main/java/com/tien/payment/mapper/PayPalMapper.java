package com.tien.payment.mapper;

import com.tien.payment.dto.request.PayPalRequest;
import com.tien.payment.dto.response.PayPalResponse;
import com.tien.payment.entity.PayPal;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PayPalMapper {

      @Mapping(target = "paymentState", ignore = true)
      @Mapping(target = "payerId", ignore = true)
      @Mapping(target = "paymentId", ignore = true)
      PayPal toPaypal(PayPalRequest request);

      PayPalResponse toPaypalResponse(PayPal entity);

}