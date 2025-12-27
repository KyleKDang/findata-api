package com.findata.api.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "alphavantage")
@Data
public class AlphaVantageProperties {

    private String apiKey;
    private String baseUrl;
}
