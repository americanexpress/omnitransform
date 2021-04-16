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

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.apache.spark.sql.Dataset;


import com.americanexpress.omnitransform.exceptions.OmniTransformConstants;
import com.americanexpress.omnitransform.exceptions.OmniTransformException;
import com.americanexpress.omnitransform.util.Configurable;
import com.americanexpress.omnitransform.util.InputTransferObject;
import com.americanexpress.omnitransform.util.Transferrable;
import com.americanexpress.omnitransform.util.Transform;
import com.americanexpress.omnitransform.util.Transformable;

public class TransformationProcessor implements Transformable {

	private static Logger logger = LogManager.getLogger(TransformationProcessor.class);

	public Transferrable transform(Configurable config, Transferrable transferIn) {

		if (transferIn == null)
			return transferIn;

		Transform trn = (Transform) config;

		try {
			Dataset result = this.execute(trn);

			for (String col : trn.getDropColumns())
				result = result.drop(col);

			result.show(2);

			HashMap<String, Dataset> hm2 = new HashMap<String, Dataset>();
			hm2.put(OmniTransformConstants.RESULT, result);

			this.createTempTables(hm2, trn.getId());
		} catch (OmniTransformException e) {
			logger.error(e.getMessage(), e);
			e.printStackTrace();
		}

		InputTransferObject ito = new InputTransferObject();
		ito.setContent("");

		return ito;

	}

}