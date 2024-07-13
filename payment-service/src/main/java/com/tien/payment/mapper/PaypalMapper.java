package com.tien.payment.mapper;

import com.tien.payment.dto.PaypalRequest;
import com.tien.payment.dto.PaypalResponse;
import com.tien.payment.entity.Paypal;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PaypalMapper {

      @Mapping(target = "paymentState", ignore = true)
      @Mapping(target = "payerId", ignore = true)
      @Mapping(target = "paymentId", ignore = true)
      Paypal toPaypal(PaypalRequest request);

      PaypalResponse toPaypalResponse(Paypal entity);

}