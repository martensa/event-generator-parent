package com.solace.amartens.eventtransformer.cryptoshredder;

import java.util.List;
import java.util.Set;
import java.util.function.Function;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.SpecVersion;
import com.networknt.schema.ValidationMessage;
import com.solace.amartens.eventportal.v2.restapiclient.SolaceEventPortalRestClient;
import com.solace.amartens.eventportal.v2.restapiclient.entities.CustomAttribute;
import com.solace.amartens.eventportal.v2.restapiclient.entities.DataObject;
import com.solace.amartens.eventportal.v2.restapiclient.entities.EventVersion;
import com.solace.amartens.eventportal.v2.restapiclient.entities.SchemaVersion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.integration.StaticMessageHeaderAccessor;
import org.springframework.integration.acks.AckUtils;
import org.springframework.integration.acks.AcknowledgmentCallback;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.vault.core.VaultTemplate;
import org.springframework.vault.core.VaultTransitOperations;

@SpringBootApplication
@EnableConfigurationProperties({SolaceCryptoShredderProperties.class})
@ComponentScan({"com.solace.amartens.eventportal.v2.restapiclient"})
public class SolaceCryptoShredder {
	private static final Logger logger = LoggerFactory.getLogger(SolaceCryptoShredder.class);

	private final SolaceCryptoShredderProperties solaceCryptoShredderProperties;
	private ObjectMapper mapper = new ObjectMapper();

	public SolaceCryptoShredder(SolaceCryptoShredderProperties solaceCryptoShredderProperties) {
		this.solaceCryptoShredderProperties = solaceCryptoShredderProperties;
	}

	public static void main(String[] args) {
		SpringApplication.run(SolaceCryptoShredder.class, args);
	}

	private Set<ValidationMessage> validateJson(JsonNode node, String schema) {
		JsonSchemaFactory factory = JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V7);
		JsonSchema jsonSchema = null;
		try {
			jsonSchema = factory.getSchema(schema);
		} catch (Exception e) {
			e.printStackTrace();
		}

		Set<ValidationMessage> errors = jsonSchema.validate(node);
		System.out.println("Errors in json object: " + errors);
		return errors;
	}

	@Bean
	public Function<Message,Message> eventToDTOConverter(VaultTemplate vaultTemplate, EventVersion targetEventVersion, SchemaVersion sourceSchemaVersion) {
		return message -> {
			AcknowledgmentCallback acknowledgmentCallback = StaticMessageHeaderAccessor.getAcknowledgmentCallback(message);
			acknowledgmentCallback.noAutoAck();

			JsonNode node = null;
			try {
				node = mapper.readTree(message.getPayload().toString());
			}
			catch (JsonProcessingException e) {
				logger.info("Parsing of incoming message failed! Message will be skipped!");
				e.printStackTrace();
				AckUtils.accept(acknowledgmentCallback);
				return null;
			}

			Message encryptedEvent = null;
			Set<ValidationMessage> validationMessages = validateJson(node, sourceSchemaVersion.getContent());
			if (validationMessages.isEmpty()) {
				VaultTransitOperations vaultTransitOperations = vaultTemplate.opsForTransit();

				List<CustomAttribute> customAttributes = targetEventVersion.getCustomAttributes();
				for (CustomAttribute attribute : customAttributes) {
					if (attribute.getName().equals("encryptSchemaProperties")) {
						String[] splits = attribute.getValue().split(" ");
						for (int i=0; i < splits.length; i++) {
							JsonNode removedNode = ((ObjectNode) node).remove(splits[i]);
							String encryptedField = vaultTransitOperations.encrypt(this.solaceCryptoShredderProperties.getVaultTransitEncryptionKey(), removedNode.textValue());
							((ObjectNode) node).put(splits[i], encryptedField);
						}
					}
				}

				encryptedEvent = MessageBuilder.withPayload(node)
						.setHeader("gdpr.compliance", "crypto-shredding")
						.build();
			}

			AckUtils.accept(acknowledgmentCallback);
			return encryptedEvent;
		};
	}

	@Bean
	public EventVersion sourceEventVersion(SolaceEventPortalRestClient solaceEventPortalRestClient) {
		try {
			DataObject<EventVersion> eventVersionDataObject = solaceEventPortalRestClient.getEventVersion(this.solaceCryptoShredderProperties.getSourceEventVersionId());
			return eventVersionDataObject.getData();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Bean
	public EventVersion targetEventVersion(SolaceEventPortalRestClient solaceEventPortalRestClient) {
		try {
			DataObject<EventVersion> eventVersionDataObject = solaceEventPortalRestClient.getEventVersion(this.solaceCryptoShredderProperties.getTargetEventVersionId());
			return eventVersionDataObject.getData();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Bean
	public SchemaVersion sourceSchemaVersion(SolaceEventPortalRestClient solaceEventPortalRestClient, EventVersion sourceEventVersion) {
		try {
			DataObject<SchemaVersion> schemaVersionDataObject = solaceEventPortalRestClient.getSchemaVersion(sourceEventVersion.getSchemaVersionId());
			return schemaVersionDataObject.getData();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Bean
	public SchemaVersion targetSchemaVersion(SolaceEventPortalRestClient solaceEventPortalRestClient, EventVersion targetEventVersion) {
		try {
			DataObject<SchemaVersion> schemaVersionDataObject = solaceEventPortalRestClient.getSchemaVersion(targetEventVersion.getSchemaVersionId());
			return schemaVersionDataObject.getData();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}