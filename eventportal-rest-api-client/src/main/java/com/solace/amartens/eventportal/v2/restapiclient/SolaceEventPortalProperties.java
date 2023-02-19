package com.solace.amartens.eventportal.v2.restapiclient;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "solace.event.portal")
public class SolaceEventPortalProperties {
	private String endpoint;
	private String token;
	private ApplicationDomain applicationDomain;
	private EventApiProduct eventApiProduct;
	private Plan plan;

	public String getToken() {
		return this.token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getEndpoint() {
		return this.endpoint;
	}

	public void setEndpoint(String endpoint) {
		this.endpoint = endpoint;
	}

	public ApplicationDomain getApplicationDomain() {
		return applicationDomain;
	}

	public void setApplicationDomain(ApplicationDomain applicationDomain) {
		this.applicationDomain = applicationDomain;
	}

	public EventApiProduct getEventApiProduct() {
		return eventApiProduct;
	}

	public void setEventApiProduct(EventApiProduct eventApiProduct) {
		this.eventApiProduct = eventApiProduct;
	}

	public Plan getPlan() {
		return plan;
	}

	public void setPlan(Plan plan) {
		this.plan = plan;
	}

	public static class ApplicationDomain {
		private String name;

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}
	}

	public static class EventApiProduct {
		private String name;
		private String version;

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getVersion() {
			return version;
		}

		public void setVersion(String version) {
			this.version = version;
		}
	}

	public static class Plan {
		private String name;

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}
	}
}