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

package com.americanexpress.omnitransform;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.americanexpress.omnitransform.engine.TransformationContext;
import com.americanexpress.omnitransform.engine.TransformationEngine;
import com.americanexpress.omnitransform.exceptions.OmniTransformException;

public class App {
	private static Logger logger = LogManager.getLogger(App.class);
	private static String env = "e0";

	public static boolean isLocalEnv() {
		return env.equals("e0");
	}

	public static void main(String[] args) {
		String configFilePath = "./src/main/resources/sales-jsl-config.json";

		if (args.length >= 1)
			env = args[0];

		if (App.isLocalEnv()) {
//			configFilePath = "./src/main/resources/suspense-jsl-config.json";

		}

		String sparkAppName = "OmniTransform Sample App";
		TransformationEngine te;
		try {
			te = new TransformationEngine(configFilePath, sparkAppName, env);
			TransformationContext context = TransformationContext.getInstance();
			context.getSparkSession().sparkContext().setLogLevel("ERROR");

			te.execute();
			context.getSparkSession().stop();

		} catch (OmniTransformException e) {
			
			logger.error(e.getMessage(), e);
			e.printStackTrace();
		}

	}

}