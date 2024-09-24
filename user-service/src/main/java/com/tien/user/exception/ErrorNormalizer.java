package com.tien.user.exception;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tien.user.dto.identity.KeyCloakError;
import feign.FeignException;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ErrorNormalizer {

    ObjectMapper objectMapper = new ObjectMapper();
    Map<String, ErrorCode> errorCodeMap = initializeErrorCodeMap();

    private Map<String, ErrorCode> initializeErrorCodeMap() {
        Map<String, ErrorCode> map = new HashMap<>();

        map.put("User exists with same username", ErrorCode.USER_EXISTED);
        map.put("User exists with same email", ErrorCode.EMAIL_EXISTED);
        map.put("User name is missing", ErrorCode.USERNAME_IS_MISSING);
        map.put("Password policy not met", ErrorCode.INVALID_PASSWORD);
        map.put("Role not found", ErrorCode.ROLE_NOT_FOUND);
        map.put("Client not found", ErrorCode.UNAUTHORIZED);
        map.put("Realm not found", ErrorCode.UNAUTHORIZED);
        map.put("Invalid username or password", ErrorCode.INVALID_USERNAME);

        return map;
    }

    public AppException handleKeyCloakException(FeignException exception) {
        log.warn("Cannot complete request", exception);

        try {
            KeyCloakError response = objectMapper.readValue(exception.contentUTF8(), KeyCloakError.class);
            String errorMessage = response.getErrorMessage();

            if (errorMessage != null && errorCodeMap.containsKey(errorMessage)) {
                return new AppException(errorCodeMap.get(errorMessage));
            }
        } catch (JsonProcessingException e) {
            log.error("Error deserializing KeyCloakError response", e);
        }

        return new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION);
    }

}