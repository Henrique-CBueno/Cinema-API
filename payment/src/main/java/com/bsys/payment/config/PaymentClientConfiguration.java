package com.bsys.payment.config;

import feign.Logger;
import feign.RequestInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

public class PaymentClientConfiguration {

    @Value("${integration.payment.api-key}")
    private String apiKey;

    @Bean
    public RequestInterceptor requestInterceptor() {

        return requestTemplate -> {
            requestTemplate.header("Authorization", "Bearer " + apiKey);
        };
    }

}
