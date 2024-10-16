package com.tien.shop.configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.security.SecurityScheme.Type;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static io.swagger.v3.oas.models.security.SecurityScheme.In.HEADER;

@Configuration
public class OpenAPIConfig {

      @Bean
      public OpenAPI shopServiceAPI() {
            return new OpenAPI()
                    .info(new Info().title("Shop Service API")
                            .description("This is the REST API for Shop Service")
                            .version("v0.0.1")
                            .license(new License().name("Apache 2.0")))
                    .addSecurityItem(new SecurityRequirement().addList("bearerAuth"))
                    .components(new io.swagger.v3.oas.models.Components()
                            .addSecuritySchemes("bearerAuth", new SecurityScheme()
                                    .type(Type.HTTP)
                                    .scheme("bearer")
                                    .in(HEADER)
                                    .name("Authorization")));
      }

}