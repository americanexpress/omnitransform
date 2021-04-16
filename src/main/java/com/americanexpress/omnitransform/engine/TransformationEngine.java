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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.spark.sql.Dataset;
import org.beanio.BeanReaderException;

import com.americanexpress.omnitransform.exceptions.OmniTransformConstants;
import com.americanexpress.omnitransform.exceptions.OmniTransformException;
import com.americanexpress.omnitransform.util.Configurable;
import com.americanexpress.omnitransform.util.Input;
import com.americanexpress.omnitransform.util.InputTransferObject;
import com.americanexpress.omnitransform.util.Output;
import com.americanexpress.omnitransform.util.Transferrable;
import com.americanexpress.omnitransform.util.Transform;
import com.americanexpress.omnitransform.util.Transformable;
import com.americanexpress.omnitransform.util.TransformationConfig;

/**
 * @author Anant Athale
 *
 */

public class TransformationEngine {

	private TransformationEngineData data = new TransformationEngineData(false);

	public HashMap<String, Object> getFlowConfigMap() {
		return data.flowConfigMap;
	}

	public TransformationEngine(String configFilePath, String sparkAppName, String env) throws OmniTransformException{
		this.init(configFilePath, sparkAppName, env);

	}
	
	
	private TransformationEngine newInstance() throws OmniTransformException
	{
		TransformationEngine te = new TransformationEngine(this.data.configFilePath, this.data.sparkAppName, this.data.env);
		te.data.recursive = true;
		return te;
	}

	private void init(String configFilePath, String sparkAppName, String env) throws OmniTransformException{
		// Parse Config JSON
		this.data.configFilePath = configFilePath;
		this.data.sparkAppName = sparkAppName;
		this.data.env = env;
		
		this.data.transformationContext = TransformationContext.getInstance(configFilePath, sparkAppName, env);
		this.data.flowConfigMap = new HashMap<String, Object>();
		this.data.flowConfigMap.put(OmniTransformConstants.SESSION,  this.data.transformationContext.getSparkSession().newSession());
	}

	
	public void execute() throws OmniTransformException{
		this.data.transformConfig = this.data.transformationContext.getTransformationConfig();
		this.data.activeList = new ArrayList<Configurable>();

		this.data.activeList = createList();
		
		processConfigurable(0, null);

	}

	// Allow executing a different config file
	public void execute(String configFilePath) throws OmniTransformException{

		this.data.transformationContext.setConfig(configFilePath);
		this.execute();
	}

	private void execute(TransformationConfig transformationConfig) throws OmniTransformException{
		this.data.transformConfig = transformationConfig;
		this.data.activeList = new ArrayList<Configurable>();

		this.data.activeList = createList();
		
		processConfigurable(0, null);
	}

	private void processConfigurable(int processIndex, Transferrable transferIn) throws OmniTransformException {

		List<Configurable> list = this.data.activeList;
		if (processIndex < 0 || processIndex > list.size()||list.isEmpty())
			return;

		String cName = OmniTransformConstants.DEFAULT;
		Configurable conf = list.get(processIndex);
		Input input = null;
		if (conf instanceof Input) {
			input = (Input) conf;

			input.setAdditionalProperty(OmniTransformConstants.FLOW_CONFIG_MAP, data.flowConfigMap);
			File source = new File(input.getInputFilePath());
			String unit = ((Input) conf).getUnit();

			if (source.isDirectory() && unit.equals(OmniTransformConstants.FILE)) {
				if (!this.data.recursive) {
					this.data.recursive = true;

					File[] file_list = source.listFiles();

					if (file_list == null)
						return;

					for (File f : file_list) {
						if (f.isFile()) {

							List<Input> l = this.data.transformConfig.getInput();
							for (Input i : l) {
								if (i.getId().equals(input.getId())) {
									i.setInputFilePath(f.getAbsolutePath());
									newInstance().execute(this.data.transformConfig);
								}
							}

						}
					}
					this.data.recursive = false;
				}

				return;
			}

			cName = input.getProcessor();
			if (OmniTransformConstants.DEFAULT.equals(cName))
				cName = OmniTransformConstants.PROCESSOR_DEFAULT_INPUT;

		} else if (conf instanceof Transform) {
			Transform transform = (Transform) conf;
			transform.setAdditionalProperty(OmniTransformConstants.FLOW_CONFIG_MAP, data.flowConfigMap);

			cName = transform.getProcessor();
			if (OmniTransformConstants.DEFAULT.equals(cName))
				cName = OmniTransformConstants.PROCESSOR_DEFAULT_TRANSFORM;
		} else if (conf instanceof Output) {
			Output output = (Output) conf;
			output.setAdditionalProperty(OmniTransformConstants.FLOW_CONFIG_MAP, data.flowConfigMap);

			cName = output.getProcessor();
			if (OmniTransformConstants.DEFAULT.equals(cName))
				cName = OmniTransformConstants.PROCESSOR_DEFAULT_OUTPUT;
		}

		String nextId = OmniTransformConstants.DEFAULT;
		Transferrable obj = null;
	
			Transformable s;
			try {
				s = (Transformable) Class.forName(cName).newInstance();
				obj = s.transform(conf, transferIn);
			} catch(BeanReaderException e) { 
				throw new OmniTransformException(OmniTransformConstants.ONET01,input.getInputFilePath(), e);
			} catch (InstantiationException e) {
				throw new OmniTransformException(OmniTransformConstants.ONET01,cName, e);
			} catch (IllegalAccessException e) {
				throw new OmniTransformException(OmniTransformConstants.ONET04,cName, e);
			} catch (ClassNotFoundException e) {
				throw new OmniTransformException(OmniTransformConstants.ONET01,cName, e);
			} 
		

		if (obj != null && obj.getNextConfigTransitionId() != null) {
			nextId = obj.getNextConfigTransitionId();
		}

		if (OmniTransformConstants.FLOW_STOP.equals(nextId)) {
			return;
		} else if (OmniTransformConstants.DEFAULT.equals(nextId)) {
			processIndex++;
			if (processIndex < list.size())
				processConfigurable(processIndex, obj);
		} else {
			int index = this.getIndexOfConfigId(nextId);
			if (index > processIndex && index < list.size())
				processConfigurable(index, obj);

		}

	}

	private List<Configurable> createList() {
		List<Configurable> al = new ArrayList<Configurable>();
		TransformationConfig tc = this.data.transformConfig;
		List<Input> ilist = tc.getInput();
		for (Input i : ilist)
			al.add(i);

		List<Transform> tlist = tc.getTransform();
		for (Transform t : tlist)
			al.add(t);

		List<Output> olist = tc.getOutput();
		for (Output o : olist)
			al.add(o);
		return al;
	}

	public Transferrable processInput(Input input) throws OmniTransformException {
		HashMap<String, HashMap<String, Dataset>> hm = new HashMap<String, HashMap<String, Dataset>>();
		String nextId = OmniTransformConstants.DEFAULT;

		//File source = new File(input.getInputFilePath());

		String id = input.getId();

		String cName = input.getProcessor();
		if (cName.equals(OmniTransformConstants.DEFAULT))
			cName = OmniTransformConstants.PROCESSOR_DEFAULT_INPUT;

		
			Transformable s;
			Transferrable obj ;
			try {
				s = (Transformable) Class.forName(cName).newInstance();
				obj = s.transform(input, null);

			} catch(BeanReaderException e) { 
				throw new OmniTransformException(OmniTransformConstants.ONET01,input.getInputFilePath(), e);
			} catch (InstantiationException e) {
				throw new OmniTransformException(OmniTransformConstants.ONET01,cName, e);
			} catch (IllegalAccessException e) {
				throw new OmniTransformException(OmniTransformConstants.ONET04,cName, e);
			} catch (ClassNotFoundException e) {
				throw new OmniTransformException(OmniTransformConstants.ONET01,cName, e);
			}
		

			if (obj != null && !(obj.getContent() instanceof String)) {
				HashMap<String, HashMap<String, Dataset>> hm1 = (HashMap<String, HashMap<String, Dataset>>) obj
						.getContent();
				hm.putAll(hm1);
				if (obj.getNextConfigTransitionId() != null)
					nextId = obj.getNextConfigTransitionId();
			}
		
		InputTransferObject ito = new InputTransferObject();
		ito.setContent(hm);
		ito.setNextConfigTransitionId(nextId);
		return ito;
	}

	public Transferrable processTransforms(Transform transform, Transferrable transferIn) throws OmniTransformException {
		HashMap<String, HashMap<String, Dataset>> hm = new HashMap<String, HashMap<String, Dataset>>();
		String nextId = OmniTransformConstants.DEFAULT;

		String id = transform.getId();

		String cName = transform.getProcessor();
		if (OmniTransformConstants.DEFAULT.equals(cName))
			cName = OmniTransformConstants.PROCESSOR_DEFAULT_TRANSFORM;

		try {
			Transformable s = (Transformable) Class.forName(cName).newInstance();

			transferIn = s.transform(transform, transferIn);

		} catch (InstantiationException | ClassNotFoundException e) {
			throw new OmniTransformException(OmniTransformConstants.ONET01,cName, e);
		} catch (IllegalAccessException e) {
			throw new OmniTransformException(OmniTransformConstants.ONET04,cName, e);
		} 

		InputTransferObject ito = new InputTransferObject();
		ito.setContent(hm);
		ito.setNextConfigTransitionId(nextId);
		return ito;
	}

	private int getIndexOfConfigId(String configId) {
		List<String> al = new ArrayList<String>();
		TransformationConfig tc = this.data.transformConfig;
		List<Input> ilist = tc.getInput();
		for (Input i : ilist)
			al.add(i.getId());

		List<Transform> tlist = tc.getTransform();
		for (Transform t : tlist)
			al.add(t.getId());

		List<Output> olist = tc.getOutput();
		for (Output o : olist)
			al.add(o.getId());

		return al.indexOf(configId);
	}

	public Transferrable processOutput(List<Output> list, Transferrable transferIn) throws OmniTransformException {
		Iterator<Output> it = list.iterator();
		while (it.hasNext()) {
			Output out = it.next();
			String id = out.getId();

			String cName = out.getProcessor();
			if (cName.equals(OmniTransformConstants.DEFAULT))
				cName = OmniTransformConstants.PROCESSOR_DEFAULT_OUTPUT;

			try {
				Transformable s = (Transformable) Class.forName(cName).newInstance();

				transferIn = s.transform(out, transferIn);

			} catch (InstantiationException | ClassNotFoundException e) {
				throw new OmniTransformException(OmniTransformConstants.ONET01,cName, e);
			} catch (IllegalAccessException e) {
				throw new OmniTransformException(OmniTransformConstants.ONET04,cName, e);
			} 
		}

		return transferIn;
	}
}
