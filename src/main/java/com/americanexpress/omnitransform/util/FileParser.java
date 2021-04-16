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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.ForeachPartitionFunction;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.RowFactory;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.StructType;
import org.beanio.BeanIOConfigurationException;
import org.beanio.BeanReader;
import org.beanio.StreamFactory;
import org.beanio.internal.config.BeanIOConfig;
import org.beanio.internal.config.ComponentConfig;
import org.beanio.internal.config.StreamConfig;
import org.beanio.internal.config.xml.XmlConfigurationLoader;

import com.americanexpress.omnitransform.engine.TransformationContext;
import com.americanexpress.omnitransform.exceptions.OmniTransformConstants;
import com.americanexpress.omnitransform.exceptions.OmniTransformException;

/**
 * @author Anant Athale
 *
 */

public class FileParser {

	private static final String FLOW_CONFIG_MAP = "flowConfigMap";
	private static final String SESSION = "session";
	private static final String DEFAULT = "default";

	public static HashMap<String, Dataset> parseJsonFile(Input input) throws OmniTransformException {
		JsonFileParser parser = new JsonFileParser();

		return parser.parse(input);
	}

	public static HashMap<String, Dataset> parseParquetFile(Input input) throws OmniTransformException {

		ParquetFileParser parser = new ParquetFileParser();

		return parser.parse(input);
	}

	public static HashMap<String, Dataset> parseFlatFile(Input input) throws OmniTransformException {

		HashMap<String, Object> configMap = (HashMap<String, Object>) input.getAdditionalProperties()
				.get(OmniTransformConstants.FLOW_CONFIG_MAP);
		SparkSession session = (SparkSession) configMap.get(OmniTransformConstants.SESSION);
		JavaSparkContext jsc = new JavaSparkContext(session.sparkContext());

		HashMap<String, Dataset> hm = initDatasets(input, session, jsc);

		File root = new File(input.getInputFilePath());
		if (root.isDirectory()) {
			File[] file_list = root.listFiles();

			if (file_list == null)
				return null;

			for (File f : file_list) {
				if (f.isFile()) {
					parseFiletoDF(input, hm, f.getAbsolutePath(), session, jsc);
				}
			}
		} else {
			parseFiletoDF(input, hm, root.getAbsolutePath(), session,jsc);

		}

		return hm;
	}

	private static HashMap<String, Dataset> initDatasets(Input input, SparkSession session, JavaSparkContext jsc)
			throws OmniTransformException {
		HashMap<String, Dataset> hm = new HashMap<String, Dataset>();

		try {
			FileInputStream fis = new FileInputStream(new File(input.getInputSchemaFilePath()));
			XmlConfigurationLoader xcl = new XmlConfigurationLoader(fis.getClass().getClassLoader());
			Collection<BeanIOConfig> bic = xcl.loadConfiguration(fis, null);

			for (BeanIOConfig b : bic) {
				List<StreamConfig> lconf = b.getStreamList();
				for (StreamConfig s : lconf) {
					for (ComponentConfig cc : s) {
						if (cc.getComponentType() == ComponentConfig.RECORD) {
							String type = cc.getName();
							ArrayList<String> al = new ArrayList<String>();

							for (ComponentConfig ccc : cc) {
								// temp change
								if (!ccc.getName().startsWith(DEFAULT))
									al.add(ccc.getName());
							}

							JavaRDD jr = jsc.emptyRDD();

							JavaRDD<Row> rows = jr.map(new Function<String, Row>() {
								public Row call(String value) {
									return RowFactory.create(value.split("//}"));
								}
							});

							rows.collect();

							java.util.Collections.sort(al);

							Dataset emptyDF = session.createDataFrame(rows, createBeanIOSchema(al));

							// temp change
							schemaMap.put(type, createBeanIOSchema(al));

							hm.put(type, emptyDF);
						}

					}

				}
			}

		} catch (FileNotFoundException e) {
			throw new OmniTransformException(OmniTransformConstants.ONET01, input.getInputFilePath(), e);
		} catch (BeanIOConfigurationException e) {
			throw new OmniTransformException(OmniTransformConstants.ONET02, input.getInputFilePath(), e);
		} catch (IOException e) {
			throw new OmniTransformException(OmniTransformConstants.ONET04, input.getInputFilePath(), e);
		} 
		return hm;
	}

	private static Map<String, StructType> schemaMap = new HashMap<String, StructType>();

	private static HashMap<String, Dataset> parseFiletoDF(Input input, HashMap<String, Dataset> parentMap,
			String filePath, SparkSession session, JavaSparkContext jsc) throws OmniTransformException {

		StreamFactory factory = StreamFactory.newInstance();
		factory.load(input.getInputSchemaFilePath());

		BeanReader in = factory.createReader(input.getStreamName(), filePath);

		Object record = null;

		Map<String, ArrayList<Row>> tempRowList = new HashMap<String, ArrayList<Row>>();
		try {
			while ((record = in.read()) != null) {

				String keyRecordName = in.getRecordName();

				ArrayList<String> valueList = new ArrayList<String>();

				Map<String, String> result = (Map<String, String>) record;

				SortedSet<String> keys = new TreeSet<>(result.keySet());

				for (String key : keys) {
					String value = result.get(key);

					if (!key.startsWith(DEFAULT)) {
						valueList.add(value == null ? " " : value);
					}
				}

				ArrayList<Row> rows = tempRowList.get(keyRecordName);

				if (rows == null) {
					rows = new ArrayList<Row>();
					tempRowList.put(keyRecordName, rows);
				}

				rows.add(RowFactory.create(valueList.toArray()));

			}
			in.close();

			addRDDToDF(parentMap, session, jsc, tempRowList);
			tempRowList.clear();

		} catch (org.beanio.BeanIOException e) {
			throw new OmniTransformException(OmniTransformConstants.ONET03, input.getInputFilePath(), e);
		} 
		return parentMap;

	}

	private static void addRDDToDF(HashMap<String, Dataset> parentMap, SparkSession session, JavaSparkContext jsc,
			Map<String, ArrayList<Row>> tempRowList) {

		for (Map.Entry<String, ArrayList<Row>> entry : tempRowList.entrySet()) {
			JavaRDD<Row> row = jsc.parallelize(entry.getValue());

			Dataset recordDF = session.createDataFrame(row, schemaMap.get(entry.getKey()));
			Dataset<Row> dsParent = parentMap.get(entry.getKey());
			dsParent = dsParent.union(recordDF);

			parentMap.put(entry.getKey(), dsParent);

		}
	}

	private static Map<String, String> partitionMap = new HashMap<String, String>();

	private static StructType createBeanIOSchema(ArrayList<String> result) {
		ArrayList<StructField> fields = new ArrayList<StructField>();

		for (String entry : result) {
			StructField field;

			field = DataTypes.createStructField(entry, DataTypes.StringType, true);
			fields.add(field);
		}
		StructType schema = DataTypes.createStructType(fields);

		return schema;
	}

}