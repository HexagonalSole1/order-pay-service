package com.vallhalatech.order_pay_service.configuration;


import feign.Logger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignConfig {

    @Bean
    Logger.Level feignLoggerLevel() {
        return Logger.Level.BASIC; // Puedes cambiar a FULL para más detalle en desarrollo
    }
}
