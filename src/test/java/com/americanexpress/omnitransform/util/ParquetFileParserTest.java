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
import org.junit.Test;

import com.americanexpress.omnitransform.engine.TransformationContextTest;
import com.americanexpress.omnitransform.engine.TransformationEngine;
import com.americanexpress.omnitransform.exceptions.OmniTransformConstants;
import com.americanexpress.omnitransform.exceptions.OmniTransformException;
import com.americanexpress.omnitransform.util.Input;
import com.americanexpress.omnitransform.util.ParquetFileParser;

public class ParquetFileParserTest {

	private TransformationEngine engine;

	@Before
	public void setUp() throws Exception {

		engine = TransformationContextTest.engine;
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
//	@Ignore
	public void testParse() throws OmniTransformException {
		Input input = new Input();
		input.setId("sample");
		input.setStreamName("stream");
		input.setProcessor("default");
		input.setInputType("J");
		input.setInputFilePath("./src/test/resources/parquetTest/sales.parquet");
		input.setAdditionalProperty(OmniTransformConstants.FLOW_CONFIG_MAP, engine.getFlowConfigMap());

		ParquetFileParser parser = new ParquetFileParser();
		
		Assert.assertThat(parser.parse(input), Matchers.instanceOf(Map.class));

	}

}
