package com.solace.amartens.eventportal.v2.eventgenerator;

import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.sdk.autoconfigure.AutoConfiguredOpenTelemetrySdk;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class SolaceEventGenerator {
    public static void main(String[] args) {
        SpringApplication.run(SolaceEventGenerator.class, args);
    }

    @Bean
    public OpenTelemetry openTelemetry() {
        //return GlobalOpenTelemetry.get();
        return AutoConfiguredOpenTelemetrySdk.initialize().getOpenTelemetrySdk();
    }
}