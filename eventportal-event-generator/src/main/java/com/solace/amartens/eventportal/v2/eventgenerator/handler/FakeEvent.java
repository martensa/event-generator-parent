package com.solace.amartens.eventportal.v2.eventgenerator.handler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import io.apptik.json.JsonElement;
import io.apptik.json.JsonObject;
import io.apptik.json.generator.JsonGenerator;
import io.apptik.json.schema.Schema;
import io.apptik.json.schema.SchemaV4;

public class FakeEvent {
	String name;
	String topic;
	Map<String, List<?>> topicParameterEnums;
	String contentType;
	String eventVersionId;
	List<String> mapSchemaPropertiesToHeader;
	List<String> mapSchemaPropertiesToSpan;
	List<String> messagingServiceConnections;
	String payloadSchema;
	JsonElement payload;

	public FakeEvent(String name, String topic, Map<String, List<?>> topicParameterEnums, String contentType, String eventVersionId, List<String> messagingServiceConnections) {
		this.name = name;
		this.topic = topic;
		this.topicParameterEnums = topicParameterEnums;
		this.contentType = contentType;
		this.eventVersionId = eventVersionId;
		this.messagingServiceConnections = messagingServiceConnections;
	}

	public String getTopic() {
		return topic;
	}

	public void setTopic(String topic) {
		this.topic = topic;
	}

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public String getEventVersionId() {
		return eventVersionId;
	}

	public void setEventVersionId(String eventVersionId) {
		this.eventVersionId = eventVersionId;
	}

	public List<String> getMessagingServiceConnections() {
		return messagingServiceConnections;
	}

	public void setMessagingServiceConnections(List<String> messagingServiceConnections) {
		this.messagingServiceConnections = messagingServiceConnections;
	}

	public List<String> getMapSchemaPropertiesToHeader() {
		return mapSchemaPropertiesToHeader;
	}

	public void setMapSchemaPropertiesToHeader(List<String> mapSchemaPropertiesToHeader) {
		this.mapSchemaPropertiesToHeader = mapSchemaPropertiesToHeader;
	}

	public List<String> getMapSchemaPropertiesToSpan() {
		return mapSchemaPropertiesToSpan;
	}

	public void setMapSchemaPropertiesToSpan(List<String> mapSchemaPropertiesToSpan) {
		this.mapSchemaPropertiesToSpan = mapSchemaPropertiesToSpan;
	}

	public String getPayloadSchema() {
		return payloadSchema;
	}

	public void setPayloadSchema(String payloadSchema) {
		this.payloadSchema = payloadSchema;
	}

	public JsonElement getPayload() {
		return payload;
	}

	public void setPayload(JsonElement payload) {
		this.payload = payload;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Map<String, List<?>> getTopicParameterEnums() {
		return topicParameterEnums;
	}

	public void setTopicParameterEnums(Map<String, List<?>> topicParameterEnums) {
		this.topicParameterEnums = topicParameterEnums;
	}

	public String getTopicStringForFakerEventInstance() {
		String topicString = "";

		String[] topicLevels = this.topic.split("/");
		for (int i=0; i < topicLevels.length; i++) {
			if (topicLevels[i].startsWith("{")) {
				String topicLevelName = topicLevels[i].substring(1, topicLevels[i].lastIndexOf("}"));
				String topicLevel = null;
				Iterator<Map.Entry<String, JsonElement>> elements = this.payload.asJsonObject().iterator();
				while (elements.hasNext()) {
					Map.Entry<String, JsonElement> entry = (Map.Entry<String, JsonElement>) elements.next();
					if (topicLevelName.toLowerCase().startsWith(entry.getKey().toLowerCase())) {
						JsonElement element = entry.getValue();
						topicLevel = element.toString();
						topicString += topicLevel;
					}
				}
				if (topicLevel == null) {
					List<?> parameterEnum = this.topicParameterEnums.get(topicLevelName);
					if ((parameterEnum != null) && (!parameterEnum.isEmpty())) {
						int randomNum = (int) ((Math.random() * (parameterEnum.size() - 0)) + 0);
						String value = parameterEnum.get(randomNum).toString();
						topicString += value;
					} else {
						topicString += topicLevels[i];
					}
				}
			} else {
				topicString += topicLevels[i];
			}
			if (i < (topicLevels.length-1)) {
				topicString += "/";
			}
		}

		return topicString;
	}

	public void generateNewFakerEventInstance() {
		if (contentType.equals("application/json")) {
			Schema schema = null;
			try {
				schema = new SchemaV4().wrap((JsonObject) JsonElement.readFrom(this.payloadSchema));
			}
			catch (IOException e) {
				throw new RuntimeException(e);
			}
			if (schema != null) {
				JsonElement event = new JsonGenerator(schema, null).generate();
				this.payload = event;
			} else {
				this.payload = null;
			}
		}
	}
}