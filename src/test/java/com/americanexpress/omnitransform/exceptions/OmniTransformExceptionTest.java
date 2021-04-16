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

package com.americanexpress.omnitransform.exceptions;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.americanexpress.omnitransform.exceptions.OmniTransformException;


public class OmniTransformExceptionTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testOmniTransformException() {
		OmniTransformException ote = new OmniTransformException();
		Assert.assertNotNull(ote);
	}

	@Test
	public void testGetSourceFile() {
		OmniTransformException ote = new OmniTransformException("hello", "hello", new NullPointerException("---"));
		Assert.assertEquals("hello",ote.getSourceFile());
	}

	@Test
	public void testSetSourceFile() {
		OmniTransformException ote = new OmniTransformException("hello", "hello", new NullPointerException("---"));
		ote.setSourceFile("hi");
		Assert.assertEquals("hi",ote.getSourceFile());
	}

}
