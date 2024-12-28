package com.tien.user.dto.request;

import com.tien.user.enums.PackageType;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class VIPUserRequestWithSession {

      @NotNull(message = "PACKAGE_TYPE_IS_REQUIRED")
      PackageType packageType;

}