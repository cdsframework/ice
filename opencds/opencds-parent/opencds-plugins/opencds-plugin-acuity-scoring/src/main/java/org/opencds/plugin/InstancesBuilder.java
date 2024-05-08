/*
 * Copyright 2016-2020 OpenCDS.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.opencds.plugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.tuple.Pair;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;

import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;

public class InstancesBuilder {

	public static Instances build(SepsisPredictionInstance instance, List<String> arffHeaderList) {
		Pair<String, LinkedHashMap<String, List<String>>> arffHeaders = getArffHeaders(arffHeaderList);

		Table<Integer, String, Number> arffData = sepsisModelToArff(arffHeaders.getRight(), instance);
		// index for OUTCOME_POA == 13
		Instances instances = createWekaInstances(arffHeaders.getLeft(), arffHeaders.getRight(), arffData, 13);
		instances.setClassIndex(instances.numAttributes() - 1);
		// System.out.println(instances.firstInstance().toString());
		return instances;
	}

	private static Pair<String, LinkedHashMap<String, List<String>>> getArffHeaders(List<String> arffHeaderList) {
		LinkedHashMap<String, List<String>> attributes = new LinkedHashMap<>();

		String relation = arffHeaderList.get(0).replaceAll(".*'(.*)'", "$1");

		for (String header : arffHeaderList) {
			if (header.contains("@attribute")) {
				String key = header.replaceAll(".+\\s(.*)\\s.+$", "$1");
				if (header.contains("{")) {
					String value = header.replaceAll(".*\\{(.*)\\}", "$1");
					List<String> items = Arrays.asList(value.split("\\s*,\\s*"));
					attributes.put(key, items);
				} else if (header.replaceAll(".+\\s.+\\s(.+)$", "$1").equals("numeric")) {
					List<String> emptyList = new ArrayList<>();
					attributes.put(key, emptyList);
				}

			}
		}

		return Pair.<String, LinkedHashMap<String, List<String>>>of(relation, attributes);
	}

	/**
	 * Transform FactList into a table following the ARFF (Attribute-Relation
	 * File Format) format
	 *
	 * Table<Row, Attribute Name, Attribute Value>
	 *
	 * @return
	 */
	private static Table<Integer, String, Number> sepsisModelToArff(LinkedHashMap<String, List<String>> arffAttributes,
			SepsisPredictionInstance instance) {

		Table<Integer, String, Number> arffData = HashBasedTable.create();

		for (Entry<String, List<String>> entry : arffAttributes.entrySet()) {
			SepsisPredictionInstance.Attribute att = SepsisPredictionInstance.Attribute.valueOf(entry.getKey());
			if (att == SepsisPredictionInstance.Attribute.OUTCOME_POA) {
				continue;
			}
			Number codeValue = null;
			if (att == null) {
				// TODO FIXME attribute in ARFF header not found in list of
				// expected attributes.
			} else {
				if (entry.getValue().isEmpty()) {
					codeValue = att.<Number>getValueFromInstance(instance);
				} else {
					codeValue = entry.getValue().indexOf(att.<String>getValueFromInstance(instance));
				}

				if (codeValue == null) {
					instance.addMissingInputValue(att,
							"Missing input value for: " + codeValue);
				} else if (codeValue.intValue() == -1) {
					instance.addInvalidInputValue(att,
							"Invalid value for input: " + codeValue);
					// reset codeValue for future handling.
					codeValue = null;
				}

				Map<Integer, Number> column = arffData.column(att.name());

				if (codeValue != null) {
					if (column.isEmpty()) {
						arffData.put(0, att.name(), codeValue);
					} else {
						int rowKey = column.size();
						arffData.put(rowKey, att.name(), codeValue);
					}
				}
			}

		}

		return arffData;
	}

	/**
	 * Based on the relation and the numeric and nominal attributes creates Weka
	 * instances. \ The missing attribute is set as '?'
	 *
	 * arffAttributes: Map <Attribute Name, List<Possible values>, e.g.,
	 * ("GENDER", ["M", "F"]) if the List is empty, the attribute will be
	 * considered as numeric
	 *
	 * arffData: Table <Row, Attribute Name, Attribute value>. If "List<Possible
	 * values>" is not empty, the "Attribute value" will contain the index of
	 * possible values e.g., (1, "GENDER", 0) where 1 is the row number and 0
	 * represents the index of the list which contains the value "M"
	 *
	 * @param realtion
	 * @param arffData
	 * @param arffAttributes
	 * @param missingAttIndex
	 *            Define the index of the attribute that is missing in the data
	 *
	 * @return
	 */
	private static Instances createWekaInstances(String relation, LinkedHashMap<String, List<String>> arffAttributes,
			Table<Integer, String, Number> arffData, int attributeToPredict) {

		// set up attributes
		FastVector attributes = new FastVector();
		for (Entry<String, List<String>> att : arffAttributes.entrySet()) {
			if (att.getValue().isEmpty()) {
				attributes.addElement(new Attribute(att.getKey()));
			} else {
				List<String> attTypes = att.getValue();
				FastVector attTypeValues = new FastVector();
				for (String attType : attTypes) {
					attTypeValues.addElement(attType);
				}
				attributes.addElement(new Attribute(att.getKey(), attTypeValues));
			}
		}
		// set up relation
		Instances instances = new Instances(relation, attributes, 1);

		// set up data
		List<Integer> missing = new ArrayList<>();
		for (int i = 0; i < arffData.rowKeySet().size(); i++) {
			double[] values = new double[instances.numAttributes()];
			for (int j = 0; j < values.length; j++) {
				Attribute att = instances.attribute(j);
				String name = att.name();

				Number value = arffData.get(i, name);
				if (value == null) {
					missing.add(j);
				} else {
					values[j] = value.doubleValue();
				}
			}
			Instance instance = new Instance(1.0, values);
			instance.setMissing(attributeToPredict);
			// set any others that are missing
			for (Integer m : missing) {
				instance.setMissing(m);
			}
			instances.add(instance);
		}
		return instances;
	}

}
