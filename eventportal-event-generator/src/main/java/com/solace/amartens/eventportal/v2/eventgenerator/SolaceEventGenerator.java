package com.solace.amartens.eventportal.v2.eventgenerator;

import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.sdk.autoconfigure.AutoConfiguredOpenTelemetrySdk;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class SolaceEventGenerator {
    public static void main(String[] args) {
        System.setProperty("otel.resource.attributes","service.name=Solace_Event_Generator");
        System.setProperty("otel.exporter.otlp.endpoint", "http://localhost:4317");
        System.setProperty("otel.traces.exporter","otlp");
        System.setProperty("otel.metrics.exporter","logging");
        System.setProperty("otel.logs.exporter","logging");
        System.setProperty("otel.propagators","solace_jms_tracecontext");
        System.setProperty("otel.bsp.schedule.delay","500");
        System.setProperty("otel.bsp.max.queue.size","1000");
        System.setProperty("otel.bsp.max.export.batch.size","5");
        System.setProperty("otel.bsp.export.timeout","10000");

        SpringApplication.run(SolaceEventGenerator.class, args);
    }

    @Bean
    public OpenTelemetry openTelemetry() {
        //return GlobalOpenTelemetry.get();
        return AutoConfiguredOpenTelemetrySdk.initialize().getOpenTelemetrySdk();
    }
}