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


import java.io.File;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.*;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.apache.commons.io.FileUtils;
import org.hamcrest.Matchers;

import com.americanexpress.omnitransform.engine.TransformationContext;
import com.americanexpress.omnitransform.engine.TransformationEngine;
import com.americanexpress.omnitransform.exceptions.OmniTransformException;

public class TransformationContextTest {

	@Rule
	public TemporaryFolder folder = new TemporaryFolder();

	private static File sampleJSL_empty;
	private static File sampleJSL_malformed;
	private static File sampleJSL_good;

	private static String appName;
	private static String env;
	private static TransformationContext tc;

	public static TransformationEngine engine;
	
	static {

		try
		{
			appName = "sample";
			env = "e0";

			engine = new TransformationEngine("./src/test/resources/sample-jsl-config.json", appName, env);
			
			tc = TransformationContext.getInstance();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

	}

	@Before
	public void setUp() throws Exception {
		sampleJSL_empty = folder.newFile("empty-jsl-config.json");

		sampleJSL_malformed = folder.newFile("malformed-jsl-config.json");
		FileUtils.writeStringToFile(sampleJSL_malformed, "\"input\": [],\"transform\": [],\"output\": []}");

		sampleJSL_good = folder.newFile("good-jsl-config.json");
		FileUtils.writeStringToFile(sampleJSL_good, "{\"input\": [],\"transform\": [],\"output\": []}");

	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testGetTransformationConfig() throws OmniTransformException {
		String path = sampleJSL_good.getPath();

		tc.setConfig(path);
		Assert.assertNotNull(tc.getTransformationConfig());
	}

	@Test
	public void testGetSparkSession() throws OmniTransformException {
		String path = sampleJSL_good.getPath();

		tc.setConfig(path);

		Assert.assertNotNull(tc.getSparkSession());
	}

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Test
	public void testGetInstanceStringStringString_emptyJSL() throws OmniTransformException {

		thrown.expect(NullPointerException.class);
		String path = sampleJSL_empty.getPath();

		tc.setConfig(path);

	}

	@Test
	public void testGetInstanceStringStringString_malformedJSL() throws OmniTransformException {
//		NullPointerException expectedCause = new NullPointerException();
//		thrown.expectCause(Matchers.is(expectedCause));

		thrown.expect(OmniTransformException.class);
		thrown.expectMessage(Matchers.startsWith("JSLException"));
		String path = sampleJSL_malformed.getPath();

		tc.setConfig(path);

	}

	@Test
	public void testGetInstance() throws OmniTransformException {
		TransformationContext tc = TransformationContext.getInstance();
		Assert.assertNotNull(tc);
	}

}
