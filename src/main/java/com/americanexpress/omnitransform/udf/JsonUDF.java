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

package com.americanexpress.omnitransform.udf;

import java.util.ArrayList;
import java.util.List;

import org.apache.spark.sql.UDFRegistration;
import org.apache.spark.sql.api.java.UDF2;
import org.apache.spark.sql.catalyst.expressions.GenericRowWithSchema;
import org.apache.spark.sql.types.DataTypes;

import com.americanexpress.omnitransform.util.OmniTransformUDF;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import scala.collection.JavaConversions;
import scala.collection.mutable.WrappedArray;

public class JsonUDF implements OmniTransformUDF {
	

	@SuppressWarnings("serial")
	public static UDF2<GenericRowWithSchema, String, String> row_to_json = new JsonUDF.RowToJson();
	
	@SuppressWarnings("serial")
	public static UDF2<WrappedArray<GenericRowWithSchema>, String, String> struct_join_to_json = new JsonUDF.StructJoinToJson();

	@SuppressWarnings("serial")
	public static UDF2<String, String, String> column_to_json = new JsonUDF.ColumnToJson();

	public void registerUDF(UDFRegistration catalog) {
		catalog.register("row_to_json", row_to_json, DataTypes.StringType);
		catalog.register("column_to_json", column_to_json, DataTypes.StringType);
		catalog.register("struct_join_to_json", struct_join_to_json, DataTypes.StringType);
	}

	static class ColumnToJson implements UDF2<String,String,String>{

		public String call(String s, String s2) throws Exception {

			JsonArray jsonArray = new JsonArray();
			jsonArray.add(s);
			jsonArray.add(s2);
			return jsonArray.toString();
		}

	}
	
	static class RowToJson implements UDF2<GenericRowWithSchema, String, String> {
		/*
		 * User Defined Function accepts a Spark SQL Struct data type and will convert it into JSON and return the response.
		 * Second parameter is name of the Json element into which you want to embedded the Struct into.
		 */

		@Override
		public String call(GenericRowWithSchema data, String rootElementName) throws Exception {

			String[] fields = data.schema().fieldNames();

			JsonObject containerJsonElement = new JsonObject();
			JsonObject json = new JsonObject();
			int i = 0;
			for (String field : fields) {
				json.addProperty(field, (String) data.values()[i]);
				i++;
			}
			if (rootElementName.isEmpty() || rootElementName.length() == 0) {
				return json.toString();
			}
			containerJsonElement.add(rootElementName, json);
			return containerJsonElement.toString();

		}
	}
	
	static class StructJoinToJson implements UDF2<WrappedArray<GenericRowWithSchema>, String, String> {
		/*
		 * User Defined Function accepts an array of Spark Struct Data types and returns as a JSON Array.
		 * Second parameter is name of the Json element into which you want to embedded the individual Struct elements.
		 * 
		 * Sample usage in JSL file --> 
		 * 1. Setup your Data as Struct Data Type e.g: 
		 * 		struct(ADDR1 as addressline1, ADDR2 as addressline2, ADDR3 as addressline3,
		 * 			   CITY as city, STATECD as state, COUNTY as country, ZIPCODE as zipcode) as address_dataObject1
		 * 		struct(ADDR1 as addressline1, ADDR2 as addressline2, ADDR3 as addressline3,
		 * 			   CITY as city, STATECD as state, COUNTY as country, ZIPCODE as zipcode) as address_dataObject2
		 * 2. Use the UDF in JSL e.g: struct_join_to_json(array(address_dataObject1 , address_dataObject2),'address') as addresses`
		 * 
		 * Output:
		 * "addresses":"[{\"address\":{\"addressline1\":\"Ap #468-9683 Diam Rd.\",\"addressline2\":\"Ap #892\",\"addressline3\":\"P.O. Box 425, 2890 Fermentum Ave\",\"city\":\"Pittsburgh\",\"state\":\"PA\",\"country\":\"USA\",\"zipcode\":\"11678\"}},
		 * 				 {\"address\":{\"addressline1\":\"Ap #468-9683 Diam Rd.\",\"addressline2\":\"Ap #2272\",\"addressline3\":\"P.O. Box 925, 2890 Sacremanto Ave\",\"city\":\"San Jose\",\"state\":\"CA\",\"country\":\"USA\",\"zipcode\":\"11678\"}}]"
		 */
		@Override
		public String call(WrappedArray<GenericRowWithSchema> data, String containerElementName) throws Exception {
			
			JsonArray jsonArrayElement = new JsonArray();
			JsonObject containerJsonElement = new JsonObject();
			
			List<GenericRowWithSchema> dataObjects = new ArrayList<>();
			JavaConversions.asJavaCollection(data).forEach(dataObject -> dataObjects.add(dataObject));

			for (GenericRowWithSchema dataObject : dataObjects) {
				String[] fields = dataObject.schema().fieldNames();
				JsonObject jsonObj = new JsonObject();
				int i = 0;
				for (String field : fields) {

					jsonObj.addProperty(field, (String) dataObject.values()[i]);
					i++;
				}
				containerJsonElement.add(containerElementName, jsonObj);
				jsonArrayElement.add(containerJsonElement);

			}
			return jsonArrayElement.toString();

		}
	}
}
