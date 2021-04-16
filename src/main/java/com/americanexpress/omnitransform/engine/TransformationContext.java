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

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.spark.SparkConf;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.SparkSession.Builder;
import org.apache.spark.streaming.Durations;
import org.apache.spark.streaming.api.java.JavaStreamingContext;

import com.americanexpress.omnitransform.exceptions.OmniTransformConstants;
import com.americanexpress.omnitransform.exceptions.OmniTransformException;
import com.americanexpress.omnitransform.util.Input;
import com.americanexpress.omnitransform.util.OmniTransformUDF;
import com.americanexpress.omnitransform.util.Output;
import com.americanexpress.omnitransform.util.Transform;
import com.americanexpress.omnitransform.util.TransformationConfig;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

/**
 * @author Anant Athale
 *
 */
public final class TransformationContext {

	private TransformationConfig config;
	private SparkSession sparkSession;
	private static TransformationContext instance = null;
	private String configFilePath;
	private HashMap<String, String> initProperties = new HashMap<String, String>();

	private TransformationContext(String configFilePath, String sparkAppName, String env) throws OmniTransformException {
		super();

		this.setConfig(configFilePath);

		// Init Spark session
		if (env == null)
			return;

		if (env.startsWith("e0")) {
			if (env.contains("hive"))
				this.sparkSession = this.initSession().appName(sparkAppName).config("spark.master", "local")
						.enableHiveSupport().getOrCreate();
			else
				this.sparkSession = this.initSession().appName(sparkAppName).config("spark.master", "local")
						.getOrCreate();

		} else if (env.contains("hive")) {
			this.sparkSession = this.initSession().appName(sparkAppName).enableHiveSupport().getOrCreate();
			this.sparkSession.sparkContext().hadoopConfiguration()
					.set("mapreduce.fileoutputcommitter.algorithm.version", "2");
			this.sparkSession.sparkContext().hadoopConfiguration()
					.set("mapreduce.fileoutputcommitter.marksuccessfuljobs", "false");
		} else {
			this.sparkSession = this.initSession().appName(sparkAppName).getOrCreate();
			this.sparkSession.sparkContext().hadoopConfiguration()
					.set("mapreduce.fileoutputcommitter.algorithm.version", "2");
			this.sparkSession.sparkContext().hadoopConfiguration()
					.set("mapreduce.fileoutputcommitter.marksuccessfuljobs", "false");

			if (env.contains("hdfs")) {
				this.sparkSession.sparkContext().hadoopConfiguration().set("fs.s3.impl",
						"org.apache.hadoop.fs.s3native.NativeS3FileSystem");
			} else {
				this.sparkSession.sparkContext().hadoopConfiguration().set("fs.defaultFS", "file:///");

				this.sparkSession.sparkContext().hadoopConfiguration().set("fs.hdfs.impl",
						org.apache.hadoop.fs.RawLocalFileSystem.class.getName());
			}
		}

	}

	protected void setConfig(String configFilePath) throws OmniTransformException {
		this.configFilePath = configFilePath;
		this.config = this.parseTransformConfig(configFilePath);
		this.preProcessConfig();
	}

	private Builder initSession() {
		Builder builder = SparkSession.builder();

		for (String key : this.initProperties.keySet()) {
			builder.config(key, this.initProperties.get(key));
		}

		return builder;

	}

	private void preProcessConfig() throws OmniTransformException {
		List<Input> inputs = this.config.getInput();
		for (Input input : inputs) {
			Map<String, Object> props = input.getAdditionalProperties();
			for (String key : props.keySet()) {
				if (key.startsWith("init.")) {
					String subKey = key.substring(5);
					this.initProperties.put(subKey, (String) props.get(key));
				}
			}
		}

		List<Output> outputs = this.config.getOutput();
		for (Output output : outputs) {
			Map<String, Object> props = output.getAdditionalProperties();
			for (String key : props.keySet()) {
				if (key.startsWith("init.")) {
					String subKey = key.substring(5);
					this.initProperties.put(subKey, (String) props.get(key));
				}
			}
		}

	}

	protected void setConfig(TransformationConfig transformationConfig) {
		this.config = transformationConfig;
	}

	public TransformationConfig getTransformationConfig() throws OmniTransformException {
		return this.parseTransformConfig(this.configFilePath);

	}

	public SparkSession getSparkSession() {
		return this.sparkSession;
	}

	public static TransformationContext getInstance(String configFilePath, String sparkAppName, String env)
			throws OmniTransformException {
		if (instance == null)
			instance = new TransformationContext(configFilePath, sparkAppName, env);

		return instance;

	}

	public static TransformationContext getInstance() throws OmniTransformException {

		return getInstance(null, null, null);
	}

	private TransformationConfig parseTransformConfig(String sourceFile) throws OmniTransformException {

		GsonBuilder builder = new GsonBuilder();
		Gson gson = builder.create();

		try (FileReader fr = new FileReader(sourceFile)) {
			return gson.fromJson(fr, TransformationConfig.class);
		} catch (FileNotFoundException | JsonSyntaxException e) {
			throw new OmniTransformException(OmniTransformConstants.ONET01, sourceFile, e);
		} catch (IOException e) {
			throw new OmniTransformException(OmniTransformConstants.ONET04, sourceFile, e);
		}
	}

}
