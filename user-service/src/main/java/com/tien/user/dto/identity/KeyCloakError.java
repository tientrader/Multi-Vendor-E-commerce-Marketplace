package com.tien.user.dto.identity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class KeyCloakError {

    @JsonProperty("error")
    String error;

    @JsonProperty("error_description")
    String errorDescription;

}