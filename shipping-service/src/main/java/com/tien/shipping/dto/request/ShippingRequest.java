package com.tien.shipping.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ShippingRequest {

    String userId;
    String firstName;
    String lastName;
    LocalDate dob;
    String city;

}