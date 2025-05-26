
// SwaggerConfig.java
package com.vallhalatech.order_pay_service.configuration;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Order Pay Service API",
                description = "Microservicio para gestión de órdenes y pagos",
                version = "1.0"
        )
)
public class SwaggerConfig {
    // La configuración básica ya está incluida con la anotación
}


