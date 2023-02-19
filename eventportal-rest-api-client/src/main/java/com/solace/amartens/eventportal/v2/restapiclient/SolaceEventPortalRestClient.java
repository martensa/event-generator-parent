package com.solace.amartens.eventportal.v2.restapiclient;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.arkea.asyncapi.v2.models.AsyncAPI;
import com.arkea.asyncapi.v2.parser.AsyncAPIV2Parser;
import com.arkea.asyncapi.v2.parser.models.AsyncParseResult;
import com.arkea.asyncapi.v2.parser.models.ParseOptions;
import com.solace.amartens.eventportal.v2.restapiclient.entities.ApplicationDomain;
import com.solace.amartens.eventportal.v2.restapiclient.entities.DataArray;
import com.solace.amartens.eventportal.v2.restapiclient.entities.DataObject;
import com.solace.amartens.eventportal.v2.restapiclient.entities.EventApiProduct;
import com.solace.amartens.eventportal.v2.restapiclient.entities.EventApiProductVersion;
import com.solace.amartens.eventportal.v2.restapiclient.entities.EventVersion;
import com.solace.amartens.eventportal.v2.restapiclient.entities.GatewayMessagingService;
import com.solace.amartens.eventportal.v2.restapiclient.entities.MessagingService;
import com.solace.amartens.eventportal.v2.restapiclient.entities.Plan;
import com.solace.amartens.eventportal.v2.restapiclient.entities.SchemaVersion;
import com.solace.amartens.eventportal.v2.restapiclient.exception.EventPortalEntityIsMissingException;
import com.solace.amartens.eventportal.v2.restapiclient.exception.EventPortalEntityIsNotFoundException;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

public class SolaceEventPortalRestClient {
	private final SolaceEventPortalProperties properties;
	private final RestTemplate restTemplate;

	public SolaceEventPortalRestClient(RestTemplate restTemplate, SolaceEventPortalProperties properties) {
		this.properties = properties;
		this.restTemplate = restTemplate;
	}

	private HttpHeaders createHeaders(){
		return new HttpHeaders() {{
			set( "Authorization", "Bearer " + properties.getToken());
		}};
	}

	private String generateParameterFromList(String parameterName, List<String> list) {
		String parameter = "";
		for (String item : list) {
			parameter += "&"+parameterName+"="+item;
		}

		return parameter;
	}

	public DataArray<ApplicationDomain> getApplicationDomains(String name) throws Exception {
		ResponseEntity<DataArray<ApplicationDomain>> domainsEntity = restTemplate.exchange
				(properties.getEndpoint()+"/api/v2/architecture/applicationDomains?pageSize=20&pageNumber=1&name="+name, HttpMethod.GET, new HttpEntity<String>(createHeaders()), new ParameterizedTypeReference<DataArray<ApplicationDomain>>(){});
		return  domainsEntity.getBody();
	}

	public DataObject<EventVersion> getEventVersion(String versionId) throws Exception {
		ResponseEntity<DataObject<EventVersion>> eventEntity = restTemplate.exchange
				(properties.getEndpoint()+"/api/v2/architecture/eventVersions/"+versionId, HttpMethod.GET, new HttpEntity<String>(createHeaders()), new ParameterizedTypeReference<DataObject<EventVersion>>(){});
		return eventEntity.getBody();
	}

	public DataObject<SchemaVersion> getSchemaVersion(String versionId) throws Exception {
		ResponseEntity<DataObject<SchemaVersion>> schemaEntity = restTemplate.exchange
				(properties.getEndpoint()+"/api/v2/architecture/schemaVersions/"+versionId, HttpMethod.GET, new HttpEntity<String>(createHeaders()), new ParameterizedTypeReference<DataObject<SchemaVersion>>(){});
		return schemaEntity.getBody();
	}

	public DataArray<EventApiProduct> getEventApiProducts(String name, String applicationDomainId) throws Exception {
		ResponseEntity<DataArray<EventApiProduct>> productsEntity = restTemplate.exchange
				(properties.getEndpoint()+"/api/v2/architecture/eventApiProducts?pageSize=20&pageNumber=1&brokerType=solace&name="+name+"&applicationDomainId="+applicationDomainId, HttpMethod.GET, new HttpEntity<String>(createHeaders()), new ParameterizedTypeReference<DataArray<EventApiProduct>>(){});
		return productsEntity.getBody();
	}

	public DataArray<EventApiProductVersion> getEventApiProductVersions(String eventApiProductId) {
		ResponseEntity<DataArray<EventApiProductVersion>> productVersionsEntity = restTemplate.exchange
				(properties.getEndpoint()+"/api/v2/architecture/eventApiProductVersions?pageSize=20&pageNumber=1&eventApiProductIds="+eventApiProductId, HttpMethod.GET, new HttpEntity<String>(createHeaders()), new ParameterizedTypeReference<DataArray<EventApiProductVersion>>(){});
		return productVersionsEntity.getBody();
	}

	public DataObject<EventApiProductVersion> getEventApiProductVersionDataObject(String eventApiProductVersionId) {
		ResponseEntity<DataObject<EventApiProductVersion>> productVersionsEntity = restTemplate.exchange
				(properties.getEndpoint()+"/api/v2/architecture/eventApiProductVersions/"+eventApiProductVersionId+"?include=parent", HttpMethod.GET, new HttpEntity<String>(createHeaders()), new ParameterizedTypeReference<DataObject<EventApiProductVersion>>(){});
		return productVersionsEntity.getBody();
	}

	public AsyncAPI getEventApiVersionsAsyncApi(String eventApiVersion, String eventApiProductVersionId, String planId, List<GatewayMessagingService> gatewayMessagingServices) {
		ResponseEntity<String> asyncApiEntity = restTemplate.exchange
				(properties.getEndpoint()+"/api/v2/architecture/eventApiVersions/"+eventApiVersion+"/asyncApi?showVersioning=true&format=json&includedExtensions=all&version=2.5.0&eventApiProductVersionId="+eventApiProductVersionId+"&planId="+planId+generateParameterFromList("gatewayMessagingServiceIds",gatewayMessagingServices.stream().map(GatewayMessagingService::getId).collect(Collectors.toList())), HttpMethod.GET, new HttpEntity<String>(createHeaders()), String.class);
		String asyncApiString = asyncApiEntity.getBody();
		AsyncAPIV2Parser asyncAPIV2Parser = new AsyncAPIV2Parser();
		ParseOptions options = new ParseOptions();
		options.setResolve(false);
		AsyncParseResult parseResult = asyncAPIV2Parser.readContents(asyncApiString,options);
		return parseResult.getAsyncAPI();
	}

	private DataArray<MessagingService> getMessagingServices(List<String> serviceIds, String type) {
		ResponseEntity<DataArray<MessagingService>> messagingServicesEntity = restTemplate.exchange
				(properties.getEndpoint()+"/api/v2/architecture/messagingServices?pageSize=20&pageNumber=1&messagingServiceType="+type+"&"+generateParameterFromList("ids",serviceIds), HttpMethod.GET, new HttpEntity<String>(createHeaders()), new ParameterizedTypeReference<DataArray<MessagingService>>(){});
		return messagingServicesEntity.getBody();
	}

	private String getApplicationDomainId (String getApplicationDomainName) throws Exception {
		DataArray<ApplicationDomain> applicationDomainDataArray = getApplicationDomains(getApplicationDomainName);
		String applicationDomainId = null;
		if (!applicationDomainDataArray.getData().isEmpty()) {
			ApplicationDomain applicationDomain = applicationDomainDataArray.getData().get(0);
			applicationDomainId = applicationDomain.getId();
		}

		if (applicationDomainId == null) {
			throw new EventPortalEntityIsNotFoundException("Application Domain is not found.");
		}

		return applicationDomainId;
	}

	private String getEventApiProductId(String eventApiProductName, String applicationDomainId) throws Exception {
		DataArray<EventApiProduct> eventApiProductDataArray = getEventApiProducts(eventApiProductName, applicationDomainId);
		String eventApiProductId = null;
		if (!eventApiProductDataArray.getData().isEmpty()) {
			EventApiProduct eventApiProduct = eventApiProductDataArray.getData().get(0);
			eventApiProductId = eventApiProduct.getId();
		}

		if (eventApiProductId == null) {
			throw new EventPortalEntityIsNotFoundException("Event Api Product is not found.");
		}

		return eventApiProductId;
	}

	public EventApiProductVersion getEventApiProductVersion(String eventApiProductVersionId) throws EventPortalEntityIsNotFoundException {
		DataObject<EventApiProductVersion> eventApiProductVersionDataObject = getEventApiProductVersionDataObject(eventApiProductVersionId);
		EventApiProductVersion eventApiProductVersion = eventApiProductVersionDataObject.getData();

		if (eventApiProductVersion == null) {
			throw new EventPortalEntityIsNotFoundException("Event Api Product Version is not found.");
		}

		return eventApiProductVersion;
	}

	public EventApiProductVersion getEventApiProductVersionByEventApiProduct(String eventApiProductId, String eapVersion) throws EventPortalEntityIsNotFoundException {
		DataArray<EventApiProductVersion> eventApiProductVersionDataArray = getEventApiProductVersions(eventApiProductId);
		EventApiProductVersion eventApiProductVersion = null;
		if (!eventApiProductVersionDataArray.getData().isEmpty()) {
			for (EventApiProductVersion productVersion: eventApiProductVersionDataArray.getData()) {
				if (productVersion.getVersion().equals(eapVersion)) {
					eventApiProductVersion = productVersion;
				}
			}
		}

		if (eventApiProductVersion == null) {
			throw new EventPortalEntityIsNotFoundException("Event Api Product Version is not found.");
		}

		return eventApiProductVersion;
	}

	public List<AsyncAPI> getEventApiVersionsAsyncApis() throws Exception {
		String applicationDomainId = getApplicationDomainId(this.properties.getApplicationDomain().getName());
		String eventApiProductId = getEventApiProductId(this.properties.getEventApiProduct().getName(), applicationDomainId);
		EventApiProductVersion eventApiProductVersion = getEventApiProductVersionByEventApiProduct(eventApiProductId, this.properties.getEventApiProduct().getVersion());
		if (eventApiProductVersion.getEventApiVersionIds().isEmpty()) {
			throw new EventPortalEntityIsMissingException("Event Api is missing.");
		}
		if (eventApiProductVersion.getPlans().isEmpty()) {
			throw new EventPortalEntityIsMissingException("Plan is missing.");
		}
		if (eventApiProductVersion.getSolaceMessagingServices().isEmpty()) {
			throw new EventPortalEntityIsMissingException("Solace Messaging Service is missing.");
		}

		Plan plan = null;
		for (Plan p : eventApiProductVersion.getPlans()) {
			if (p.getName().equals(this.properties.getPlan().getName())) {
				plan = p;
			}
		}
		if (plan == null) {
			throw new EventPortalEntityIsNotFoundException("Plan is not found.");
		}

		List<AsyncAPI> asyncApis = new ArrayList<AsyncAPI>();
		for (String eventApiVersionId : eventApiProductVersion.getEventApiVersionIds()) {
			asyncApis.add(getEventApiVersionsAsyncApi(eventApiVersionId, eventApiProductVersion.getId(), plan.getId(), eventApiProductVersion.getSolaceMessagingServices()));
		}

		return asyncApis;
	}

	public List<MessagingService> getMessagingServices(EventApiProductVersion eventApiProductVersion) throws Exception {
		//String applicationDomainId = getApplicationDomainId(this.properties.getApplicationDomain().getName());
		//String eventApiProductId = getEventApiProductId(this.properties.getEventApiProduct().getName(), applicationDomainId);
		//EventApiProductVersion eventApiProductVersion = getEventApiProductVersion(eventApiProductId);
		if (eventApiProductVersion.getSolaceMessagingServices().isEmpty()) {
			throw new EventPortalEntityIsMissingException("Solace Messaging Service is missing.");
		}

		DataArray<MessagingService> messagingServices = getMessagingServices(eventApiProductVersion.getSolaceMessagingServices().stream().map(GatewayMessagingService::getMessagingServiceId).collect(Collectors.toList()), "solace");
		return messagingServices.getData();
	}
}