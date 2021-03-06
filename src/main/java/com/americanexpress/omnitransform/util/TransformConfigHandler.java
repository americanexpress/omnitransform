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

import java.util.List;
import java.util.Map;

import org.apache.spark.sql.SparkSession;

import com.americanexpress.omnitransform.exceptions.OmniTransformConstants;
import com.americanexpress.omnitransform.exceptions.OmniTransformException;

/**
 * @author Anant Athale @anant.athale@aexp.com
 *
 */

public class TransformConfigHandler {

	public static void handleConfig(Map<String, Object> props, SparkSession session) throws OmniTransformException {
		String udfClass = "";
		for (String key : props.keySet()) {
			if (key.startsWith("udf")) {
				List<String> classNames = (List<String>) props.get(key);

				for (String className : classNames) {
					udfClass = className;
					if (!udfClass.equals("")) {
						try {
							OmniTransformUDF udf = (OmniTransformUDF) (Class.forName(udfClass).newInstance());
							udf.registerUDF(session.sqlContext().udf());
						} catch (Exception e) {
							throw new OmniTransformException(OmniTransformConstants.ONET01, "Invalid UDF configuration",
									e);
						}
					}

					udfClass = "";

				}
			}
		}
	}
}
