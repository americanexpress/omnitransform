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
import java.util.List;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "id", "outputType", "saveMode", "outputFilePath", "outputSchemaFilePath", "dependentIds",
		"outputPartitionKeys", "processor" })
public class Output implements Configurable {
	@JsonProperty("id")
	private String id;
	@JsonProperty("outputType")
	private String outputType;
	@JsonProperty("saveMode")
	private String saveMode;
	@JsonProperty("outputFilePath")
	private String outputFilePath;
	@JsonProperty("outputSchemaFilePath")
	private String outputSchemaFilePath;
	@JsonProperty("dependentIds")
	private List<String> dependentIds = null;
	@JsonProperty("outputPartitionKeys")
	private List<String> outputPartitionKeys = null;
	@JsonProperty("processor")
	private String processor;
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

	@JsonProperty("saveMode")
	public String getSaveMode() {
		return saveMode;
	}

	@JsonProperty("saveMode")
	public void setSaveMode(String saveMode) {
		this.saveMode = saveMode;
	}

	@JsonProperty("outputType")
	public String getOutputType() {
		return outputType;
	}

	@JsonProperty("outputType")
	public void setOutputType(String outputType) {
		this.outputType = outputType;
	}

	@JsonProperty("outputFilePath")
	public String getOutputFilePath() {
		return outputFilePath;
	}

	@JsonProperty("outputFilePath")
	public void setOutputFilePath(String outputFilePath) {
		this.outputFilePath = outputFilePath;
	}

	@JsonProperty("outputSchemaFilePath")
	public String getOutputSchemaFilePath() {
		return outputSchemaFilePath;
	}

	@JsonProperty("outputSchemaFilePath")
	public void setOutputSchemaFilePath(String outputSchemaFilePath) {
		this.outputSchemaFilePath = outputSchemaFilePath;
	}

	@JsonProperty("dependentIds")
	public List<String> getDependentIds() {
		return dependentIds;
	}

	@JsonProperty("dependentIds")
	public void setDependentIds(List<String> dependentIds) {
		this.dependentIds = dependentIds;
	}

	@JsonProperty("outputPartitionKeys")
	public List<String> getOutputPartitionKeys() {
		return outputPartitionKeys;
	}

	@JsonProperty("outputPartitionKeys")
	public void setOutputPartitionKey(List<String> outputPartitionKeys) {
		this.outputPartitionKeys = outputPartitionKeys;
	}

	@JsonProperty("processor")
	public String getProcessor() {
		return processor;
	}

	@JsonProperty("processor")
	public void setProcessor(String processor) {
		this.processor = processor;
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