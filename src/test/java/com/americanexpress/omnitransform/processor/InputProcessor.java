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


import java.util.HashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.spark.sql.Dataset;

import org.apache.spark.sql.SparkSession;

import com.americanexpress.omnitransform.engine.TransformationContext;
import com.americanexpress.omnitransform.exceptions.OmniTransformConstants;
import com.americanexpress.omnitransform.exceptions.OmniTransformException;
import com.americanexpress.omnitransform.util.Configurable;
import com.americanexpress.omnitransform.util.FileParser;
import com.americanexpress.omnitransform.util.Input;
import com.americanexpress.omnitransform.util.InputTransferObject;
import com.americanexpress.omnitransform.util.Transferrable;
import com.americanexpress.omnitransform.util.Transformable;

public class InputProcessor implements Transformable {

	private static Logger logger = LogManager.getLogger(InputProcessor.class);

	public Transferrable transform(Configurable config, Transferrable transferIn) {
		Input ip = (Input) config;

		String inputType = ip.getInputType();

		HashMap<String,Object> configMap = (HashMap<String,Object>)ip.getAdditionalProperties().get(OmniTransformConstants.FLOW_CONFIG_MAP);
		SparkSession session = (SparkSession)(configMap.get(OmniTransformConstants.SESSION));

		logger.error("hello elf");
		try
		{
			if ("F".equals(inputType)) {
				// Parse flatfile
				return parseFlatfile(ip, transferIn);

			} else if ("J".equals(inputType)) {
				// Parse json
				return parseJsonFile(ip, transferIn);

			} else if ("P".equals(inputType)) {
				// Parse parquet
				return parseParquet(ip, transferIn);
			}
			
		}
		catch(OmniTransformException e)
		{
			logger.error(e.getMessage(), e);

			e.printStackTrace();
		}


		return null;
	}

	private Transferrable parseParquet(Input input, Transferrable transferIn) throws OmniTransformException {
		HashMap<String, Dataset> hm = FileParser.parseParquetFile(input);

		String id = input.getId();
		this.createTempTables(hm, id);

		InputTransferObject ito = new InputTransferObject();
		ito.setContent("");
		return ito;

	}

	public InputTransferObject parseJsonFile(Input input, Transferrable transferIn) throws OmniTransformException{
		HashMap<String, Dataset> hm = FileParser.parseJsonFile(input);

		String id = input.getId();

		this.createTempTables(hm, id);

		InputTransferObject ito = new InputTransferObject();
		ito.setContent("");
		return ito;
	}

	private Transferrable parseFlatfile(Input input, Transferrable transferIn) throws OmniTransformException{
		TransformationContext context = TransformationContext.getInstance();

		HashMap<String, Dataset> hm = FileParser.parseFlatFile(input);

		String id = input.getId();

		this.createTempTables(hm, id);

		InputTransferObject ito = new InputTransferObject();
		ito.setContent("");
		return ito;

	}


}