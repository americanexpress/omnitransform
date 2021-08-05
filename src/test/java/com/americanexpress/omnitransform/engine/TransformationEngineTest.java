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

package com.americanexpress.omnitransform.engine;

import static org.junit.Assert.*;

import java.awt.List;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.TemporaryFolder;

import com.americanexpress.omnitransform.engine.TransformationEngine;
import com.americanexpress.omnitransform.exceptions.OmniTransformConstants;
import com.americanexpress.omnitransform.exceptions.OmniTransformException;
import com.americanexpress.omnitransform.util.Input;
import com.americanexpress.omnitransform.util.InputTransferObject;
import com.americanexpress.omnitransform.util.Output;
import com.americanexpress.omnitransform.util.Transferrable;
import com.americanexpress.omnitransform.util.Transform;

import org.hamcrest.Matchers;
/**
 * @author Anant Athale @anant.athale@aexp.com
 *
 */

public class TransformationEngineTest {

	@Rule
	public TemporaryFolder folder = new TemporaryFolder();

	private TransformationEngine engine;

	@Before
	public void setUp() throws Exception {

		engine = TransformationContextTest.engine;
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testGetFlowConfigMap() {
		HashMap<String, Object> map = engine.getFlowConfigMap();

		Assert.assertNotNull(map.get(OmniTransformConstants.SESSION));
	}

	@Test
	public void testTransformationEngine() {
		Assert.assertNotNull(engine);
	}

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Test
	public void testExecuteString() throws IOException, OmniTransformException {
		File sampleJSL_empty = folder.newFile("empty-jsl-config.json");

		thrown.expect(NullPointerException.class);
		engine.execute(sampleJSL_empty.getPath());
	}

	@Test
	public void testExecuteString_malformedjsl() throws IOException, OmniTransformException {
		File sampleJSL_malformed = folder.newFile("malformed-jsl-config.json");
		FileUtils.writeStringToFile(sampleJSL_malformed, "\"input\": [],\"transform\": [],\"output\": []}");

		thrown.expect(OmniTransformException.class);
		engine.execute(sampleJSL_malformed.getPath());
	}

	@Test
	public void testExecuteString_goodjsl() throws IOException, OmniTransformException {
		engine.execute("./src/test/resources/sample-jsl-config.json");
		Assert.assertNotNull(engine);
	}

	@Test
	public void testExecuteString_goodjslFolder() throws IOException, OmniTransformException {

		engine.execute("./src/test/resources/sampleFolder-jsl-config.json");
		Assert.assertNotNull(engine);
	}
	
	@Test
	public void testProcessInputFolder() throws IOException, OmniTransformException {

		Input input = new Input();
		input.setId("sample");
		input.setStreamName("stream");
		input.setProcessor("default");
		input.setInputType("J");
		input.setUnit(OmniTransformConstants.FILE);
		input.setInputFilePath("./src/test/resources/jsonTest/");
		input.setAdditionalProperty(OmniTransformConstants.FLOW_CONFIG_MAP, engine.getFlowConfigMap());

		Assert.assertThat(engine.processInput(input), Matchers.instanceOf(Transferrable.class));
	}
	
	@Test
	public void testProcessInput() throws IOException, OmniTransformException {

		Input input = new Input();
		input.setId("sample");
		input.setStreamName("stream");
		input.setProcessor("default");
		input.setInputType("J");
		input.setInputFilePath("./src/test/resources/sample.json");
		input.setAdditionalProperty(OmniTransformConstants.FLOW_CONFIG_MAP, engine.getFlowConfigMap());

		Assert.assertThat(engine.processInput(input), Matchers.instanceOf(Transferrable.class));
	}
	

	@Test
	public void testProcessTransforms() throws OmniTransformException {
		Transform transform = new Transform();
		transform.setProcessor("default");
		transform.setTransformCondition("select * from sample_stream");
		transform.setId("sampleTransform");
		transform.setAdditionalProperty(OmniTransformConstants.FLOW_CONFIG_MAP, engine.getFlowConfigMap());

		InputTransferObject ito = new InputTransferObject();

		Assert.assertThat(engine.processTransforms(transform, ito), Matchers.instanceOf(Transferrable.class));

	}

	@Test
	public void testProcessTransformOutput() throws OmniTransformException {
		Output output = new Output();
		output.setProcessor("default");
		output.setOutputType("J");
		output.setOutputFilePath("./src/test/resources/sample");
		output.setId("output");
		output.setSaveMode("APPEND");
		ArrayList<String> ids = new ArrayList<String>();
		ids.add("sampleTransform");
		output.setDependentIds(ids);
		ArrayList<String> partitionKeys = new ArrayList<String>();
		
		output.setOutputPartitionKey(partitionKeys);
		output.setAdditionalProperty(OmniTransformConstants.FLOW_CONFIG_MAP, engine.getFlowConfigMap());

		InputTransferObject ito = new InputTransferObject();
		ito.setContent("");
		ArrayList<Output> list = new ArrayList<Output>();
		list.add(output);
		
		Assert.assertThat(engine.processOutput(list, ito), Matchers.instanceOf(Transferrable.class));
		
	}

}
