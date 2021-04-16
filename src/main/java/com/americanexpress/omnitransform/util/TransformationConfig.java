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
@JsonPropertyOrder({
"input",
"transform",
"output"
})
public class TransformationConfig{

@JsonProperty("input")
private List<Input> input = null;
@JsonProperty("transform")
private List<Transform> transform = null;
@JsonProperty("output")
private List<Output> output = null;
@JsonIgnore
private Map<String, Object> additionalProperties = new HashMap<String, Object>();

@JsonProperty("input")
public List<Input> getInput() {
return input;
}

@JsonProperty("input")
public void setInput(List<Input> input) {
this.input = input;
}

@JsonProperty("transform")
public List<Transform> getTransform() {
return transform;
}

@JsonProperty("transform")
public void setTransform(List<Transform> transform) {
this.transform = transform;
}

@JsonProperty("output")
public List<Output> getOutput() {
return output;
}

@JsonProperty("output")
public void setOutput(List<Output> output) {
this.output = output;
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
