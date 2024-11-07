package com.tien.file.configuration;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CustomAuthoritiesConverter implements Converter<Jwt, Collection<GrantedAuthority>> {

      private static final String REALM_ACCESS = "realm_access";
      private static final String ROLE_PREFIX = "ROLE_";
      private static final String ROLES = "roles";

      @Override
      public Collection<GrantedAuthority> convert(Jwt source) {
            Map<String, Object> realmAccessMap = source.getClaimAsMap(REALM_ACCESS);

            Object rolesObject = realmAccessMap.get(ROLES);
            if (rolesObject instanceof List<?> rolesList) {

                  return rolesList.stream()
                          .filter(role -> role instanceof String)
                          .map(role -> new SimpleGrantedAuthority(ROLE_PREFIX + role))
                          .collect(Collectors.toList());
            }

            return List.of();
      }

}