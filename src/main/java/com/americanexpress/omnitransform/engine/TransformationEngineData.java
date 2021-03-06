
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

import java.util.HashMap;
import java.util.List;

import com.americanexpress.omnitransform.util.Configurable;
import com.americanexpress.omnitransform.util.TransformationConfig;

/**
 * @author Anant Athale @anant.athale@aexp.com
 *
 */

public class TransformationEngineData {
	public TransformationContext transformationContext;
	public List<Configurable> activeList;
	public boolean recursive;
	public HashMap<String, Object> flowConfigMap;
	public TransformationConfig transformConfig;
	public String configFilePath;
	public String sparkAppName;
	public String env;

	public TransformationEngineData(boolean recursive) {
		this.recursive = recursive;
	}
}