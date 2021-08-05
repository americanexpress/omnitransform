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

import java.io.File;
import java.util.HashMap;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.SparkSession;

import com.americanexpress.omnitransform.exceptions.OmniTransformConstants;
import com.americanexpress.omnitransform.exceptions.OmniTransformException;

/**
 * @author Anant Athale @anant.athale@aexp.com
 *
 */

public class JsonFileParser implements Parser {

	@Override
	public HashMap<String, Dataset> parse(Input input) throws OmniTransformException {
		HashMap<String, Object> configMap = (HashMap<String, Object>) input.getAdditionalProperties()
				.get(OmniTransformConstants.FLOW_CONFIG_MAP);
		SparkSession session = (SparkSession) configMap.get(OmniTransformConstants.SESSION);
		File sourceFile = new File(input.getInputFilePath());

		Dataset ds = session.read().json(input.getInputFilePath()).toDF();

		HashMap<String, Dataset> hm1 = new HashMap<String, Dataset>();
		hm1.put(input.getStreamName(), ds);
		return hm1;
	}

}
