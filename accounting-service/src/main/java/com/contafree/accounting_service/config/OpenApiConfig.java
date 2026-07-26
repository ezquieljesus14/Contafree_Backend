package com.contafree.accounting_service.config;

import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;

@Configuration
@OpenAPIDefinition(
    info = @Info(
        title = "ContaFree — Accounting Service",
        version = "1.0",
        description = "Gestión contable: plan de cuentas, diarios y asientos contables"
    )
)
@SecurityScheme(
    name = "bearerAuth",
    type = SecuritySchemeType.HTTP,
    scheme = "bearer",
    bearerFormat = "JWT",
    description = "Access token obtenido en POST /login (auth-service)"
)
public class OpenApiConfig {

    @Bean
    OpenApiCustomizer removeLinksCustomizer() {
        return openApi -> {
            if (openApi.getPaths() != null) {
                openApi.getPaths().values().forEach(pathItem ->
                    pathItem.readOperations().forEach(operation ->
                        operation.getResponses().values().forEach(response ->
                            response.setLinks(null)
                        )
                    )
                );
            }
            if (openApi.getComponents() != null) {
                openApi.getComponents().setLinks(null);
            }
        };
    }
}
