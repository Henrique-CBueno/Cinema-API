package com.bsys.tickets.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "ticket")
@Getter
@Setter
public class TicketJwtProperties {

    private String jwtSecret;
    private long jwtExpiration;
}
