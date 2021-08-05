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


/**
 * @author Anant Athale @anant.athale@aexp.com
 *
 */

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "id", "dependentIds", "transformCondition", "dropColumns", "processor" })
public class Transform implements Configurable {

	@JsonProperty("id")
	private String id;
	@JsonProperty("dependentIds")
	private List<String> dependentIds = null;
	@JsonProperty("transformCondition")
	private String transformCondition;
	@JsonProperty("dropColumns")
	private List<String> dropColumns;
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

	@JsonProperty("dropColumns")
	public List<String> getDropColumns() {
		return dropColumns;
	}

	@JsonProperty("dropColumns")
	public void setDropColumns(List<String> dropColumns) {
		this.dropColumns = dropColumns;
	}

	@JsonProperty("dependentIds")
	public List<String> getDependentIds() {
		return dependentIds;
	}

	@JsonProperty("dependentIds")
	public void setDependentIds(List<String> dependentIds) {
		this.dependentIds = dependentIds;
	}

	@JsonProperty("transformCondition")
	public String getTransformCondition() {
		return transformCondition;
	}

	@JsonProperty("transformCondition")
	public void setTransformCondition(String transformCondition) {
		this.transformCondition = transformCondition;
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
