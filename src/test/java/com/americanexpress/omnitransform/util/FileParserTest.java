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

import static org.junit.Assert.*;

import java.util.Map;

import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.americanexpress.omnitransform.engine.TransformationContextTest;
import com.americanexpress.omnitransform.engine.TransformationEngine;
import com.americanexpress.omnitransform.exceptions.OmniTransformConstants;
import com.americanexpress.omnitransform.exceptions.OmniTransformException;
import com.americanexpress.omnitransform.util.FileParser;
import com.americanexpress.omnitransform.util.Input;

public class FileParserTest {

	private TransformationEngine engine;
	@Rule
	public TemporaryFolder folder = new TemporaryFolder();

	@Before
	public void setUp() throws Exception {

		engine = TransformationContextTest.engine;
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test	
	public void testParseFirstParquetFile() throws OmniTransformException {
		Input input = new Input();
		input.setId("sample");
		input.setStreamName("stream");
		input.setProcessor("default");
		input.setInputType("J");
		input.setInputFilePath("./src/test/resources/parquetTest");
		input.setAdditionalProperty(OmniTransformConstants.FLOW_CONFIG_MAP, engine.getFlowConfigMap());

		
		Assert.assertThat(FileParser.parseParquetFile(input), Matchers.instanceOf(Map.class));
	}

	@Test
//	@Ignore
	public void testParseFlatFile() throws OmniTransformException {
		Input input = new Input();
		input.setId("salesFlatFile");
		input.setStreamName("sales");
		input.setProcessor("default");
		input.setInputType("F");
		input.setInputFilePath("./src/test/resources/salesFlatFile");
		input.setInputSchemaFilePath("./src/test/resources/schema.xml");
		input.setAdditionalProperty(OmniTransformConstants.FLOW_CONFIG_MAP, engine.getFlowConfigMap());

		
		Assert.assertThat(FileParser.parseFlatFile(input), Matchers.instanceOf(Map.class));
		}

}
