package com.solace.amartens.eventportal.v2.eventgenerator.configuration;

import com.solace.amartens.eventportal.v2.SolaceEventBrokerProperties;
import com.solace.amartens.eventportal.v2.eventgenerator.handler.SolaceAsyncApiHandler;
import com.solace.amartens.eventportal.v2.SolaceEventPortalProperties;
import com.solace.amartens.eventportal.v2.SolaceEventPortalRestClient;
import com.solace.amartens.eventportal.v2.eventgenerator.publisher.SolaceFakeEventPublisher;
import io.opentelemetry.api.OpenTelemetry;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.client.RestTemplate;

@Configuration
@EnableScheduling
@EnableConfigurationProperties({SolaceEventPortalProperties.class, SolaceEventBrokerProperties.class})
public class SolaceAutoConfiguration {

	private final SolaceEventPortalProperties eventPortalProperties;

	public SolaceAutoConfiguration(SolaceEventPortalProperties eventPortalProperties) {
		this.eventPortalProperties = eventPortalProperties;
	}

	@Bean
	public RestTemplate restTemplate(RestTemplateBuilder builder) {
		return builder.build();
	}

	@Bean(value = "solaceEventPortalRestClient")
	@ConditionalOnMissingBean
	public SolaceEventPortalRestClient solaceEventPortalRestClient(RestTemplate restTemplate) {
		SolaceEventPortalRestClient solaceEventPortalRestClient = new SolaceEventPortalRestClient(restTemplate, this.eventPortalProperties);
		return solaceEventPortalRestClient;
	}

	@Bean(value = "solaceAsyncApiHandler")
	@ConditionalOnMissingBean
	public SolaceAsyncApiHandler solaceAsyncApiHandler(SolaceEventPortalRestClient solaceEventPortalRestClient) throws Exception {
		SolaceAsyncApiHandler solaceAsyncApiHandler = SolaceAsyncApiHandler.builder().solaceEventPortalRestClient(solaceEventPortalRestClient).build();
		return solaceAsyncApiHandler;
	}

	@Bean(value = "solaceFakeEventPublisher", destroyMethod = "terminate")
	@ConditionalOnMissingBean
	public SolaceFakeEventPublisher solaceFakeEventPublisher(OpenTelemetry openTelemetry, SolaceAsyncApiHandler solaceAsyncApiHandler) {
		SolaceFakeEventPublisher solaceFakeEventPublisher = SolaceFakeEventPublisher.builder().openTelemetry(openTelemetry).solaceAsyncApiHandler(solaceAsyncApiHandler).build();
		solaceFakeEventPublisher.start();
		return solaceFakeEventPublisher;
	}
}