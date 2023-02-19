package com.solace.amartens.eventportal.v2.restapiclient.configuration;

import com.solace.amartens.eventportal.v2.restapiclient.SolaceEventPortalProperties;
import com.solace.amartens.eventportal.v2.restapiclient.SolaceEventPortalRestClient;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
@EnableConfigurationProperties({SolaceEventPortalProperties.class})
public class SolaceEventPortalAutoConfiguration {

	private final SolaceEventPortalProperties eventPortalProperties;

	public SolaceEventPortalAutoConfiguration(SolaceEventPortalProperties eventPortalProperties) {
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
}