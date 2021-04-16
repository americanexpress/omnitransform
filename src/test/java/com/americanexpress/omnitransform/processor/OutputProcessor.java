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

package com.americanexpress.omnitransform.processor;

import java.util.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.spark.sql.*;

import com.americanexpress.omnitransform.exceptions.OmniTransformConstants;
import com.americanexpress.omnitransform.util.Configurable;
import com.americanexpress.omnitransform.util.InputTransferObject;
import com.americanexpress.omnitransform.util.Output;
import com.americanexpress.omnitransform.util.Transferrable;
import com.americanexpress.omnitransform.util.Transformable;

import scala.collection.JavaConverters;
import scala.collection.Seq;

import java.io.Serializable;

public class OutputProcessor implements Transformable, Serializable {

	private static Logger logger = LogManager.getLogger(OutputProcessor.class);

	public Transferrable transform(Configurable config, Transferrable transferIn) {

		if (transferIn == null || transferIn.getContent() == null)
			return transferIn;

		Output out = (Output) config;

		String[] keyArr = new String[out.getOutputPartitionKeys().size()];
		keyArr = out.getOutputPartitionKeys().toArray(keyArr);

		out.getOutputFilePath();
		String outputType = out.getOutputType();

		HashMap<String, Object> configMap = (HashMap<String, Object>) out.getAdditionalProperties()
				.get(OmniTransformConstants.FLOW_CONFIG_MAP);
		SparkSession session = (SparkSession) (configMap.get(OmniTransformConstants.SESSION));
		SQLContext sqlContext = session.sqlContext();

		if ("J".equals(outputType)) {
			// Write json

			for (String key : out.getDependentIds()) {
				Dataset ds = sqlContext.sql("select * from " + key + "_result");

				if (out.getSaveMode().equalsIgnoreCase("APPEND"))
					ds.write().partitionBy(keyArr).mode(SaveMode.Append).json(out.getOutputFilePath());
				else
					ds.write().partitionBy(keyArr).mode(SaveMode.Overwrite).json(out.getOutputFilePath());

			}
		
		}

		InputTransferObject ito = new InputTransferObject();
		ito.setContent("");

		return transferIn;
	}


}