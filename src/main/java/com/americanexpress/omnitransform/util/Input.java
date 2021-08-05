/*
 * Copyright 2020 American Express Travel Related Services Company, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */

package com.americanexpress.omnitransform.util;

import java.util.HashMap;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * @author Anant Athale @anant.athale@aexp.com
 *
 */

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "id", "inputType", "inputFilePath", "inputSchemaFilePath", "streamName", "processor" })
public class Input implements Configurable {

	@JsonProperty("id")
	private String id;
	@JsonProperty("inputType")
	private String inputType;
	@JsonProperty("inputFilePath")
	private String inputFilePath;
	@JsonProperty("inputSchemaFilePath")
	private String inputSchemaFilePath;
	@JsonProperty("streamName")
	private String streamName;
	@JsonProperty("processor")
	private String processor;
	@JsonProperty("unit")
	private String unit;
	@JsonIgnore
	private Map<String, Object> additionalProperties = new HashMap<String, Object>();

	@JsonProperty("id")
	public String getId() {
		return id;
	}

	@JsonProperty("id")
	public void setId(String id) {
		this.id = id;
	}

	@JsonProperty("inputType")
	public String getInputType() {
		return inputType;
	}

	@JsonProperty("inputType")
	public void setInputType(String inputType) {
		this.inputType = inputType;
	}

	@JsonProperty("inputFilePath")
	public String getInputFilePath() {
		return inputFilePath;
	}

	@JsonProperty("inputFilePath")
	public void setInputFilePath(String inputFilePath) {
		this.inputFilePath = inputFilePath;
	}

	@JsonProperty("inputSchemaFilePath")
	public String getInputSchemaFilePath() {
		return inputSchemaFilePath;
	}

	@JsonProperty("inputSchemaFilePath")
	public void setInputSchemaFilePath(String inputSchemaFilePath) {
		this.inputSchemaFilePath = inputSchemaFilePath;
	}

	@JsonProperty("streamName")
	public String getStreamName() {
		return streamName;
	}

	@JsonProperty("streamName")
	public void setStreamName(String streamName) {
		this.streamName = streamName;
	}

	@JsonProperty("processor")
	public String getProcessor() {
		return processor;
	}

	@JsonProperty("processor")
	public void setProcessor(String processor) {
		this.processor = processor;
	}

	@JsonProperty("unit")
	public String getUnit() {
		return this.unit;
	}

	@JsonProperty("unit")
	public void setUnit(String unit) {
		this.unit = unit;
	}

	@JsonProperty("recursive")
	public void setRecursive(String unit) {
		this.unit = unit;
	}

	@JsonAnyGetter
	public Map<String, Object> getAdditionalProperties() {
		return this.additionalProperties;
	}

	@JsonAnySetter
	public void setAdditionalProperty(String name, Object value) {
		this.additionalProperties.put(name, value);
	}

}
