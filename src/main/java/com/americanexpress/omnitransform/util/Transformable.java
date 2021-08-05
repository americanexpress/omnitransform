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

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.SQLContext;
import org.apache.spark.sql.SparkSession;

import com.americanexpress.omnitransform.engine.TransformationContext;
import com.americanexpress.omnitransform.exceptions.OmniTransformConstants;
import com.americanexpress.omnitransform.exceptions.OmniTransformException;

/**
 * @author Anant Athale @anant.athale@aexp.com
 *
 */

public interface Transformable {

	Transferrable transform(Configurable config, Transferrable transferIn);

	default public Dataset execute(Transform trn) throws OmniTransformException {

		HashMap<String, Object> configMap = (HashMap<String, Object>) trn.getAdditionalProperties()
				.get(OmniTransformConstants.FLOW_CONFIG_MAP);
		SparkSession session = (SparkSession) (configMap.get(OmniTransformConstants.SESSION));

		TransformConfigHandler.handleConfig(trn.getAdditionalProperties(), session);

		return session.sql(trn.getTransformCondition());
	}

	default public void createTempTables(HashMap<String, Dataset> hm1, String hkey) throws OmniTransformException {
		TransformationContext context;
		context = TransformationContext.getInstance();
		SQLContext sql = context.getSparkSession().sqlContext();

		if (!hm1.isEmpty()) {
			for (String key : hm1.keySet()) {
				Dataset value = hm1.get(key);
				if (value != null) {
					String tempTableName = hkey + "_" + key;

					value.createOrReplaceTempView(tempTableName);

				}
			}
		}
	}

}
