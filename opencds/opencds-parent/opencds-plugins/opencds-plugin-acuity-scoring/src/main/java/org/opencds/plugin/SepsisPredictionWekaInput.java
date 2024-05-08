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

import java.util.List;

import org.opencds.service.weka.WekaInput;

import weka.core.Instances;

public class SepsisPredictionWekaInput implements WekaInput {

	private final SepsisPredictionInstance instance;

	public SepsisPredictionWekaInput(SepsisPredictionInstance instance) {
		this.instance = instance;
	}
	
	public SepsisPredictionInstance getInstance() {
		return instance;
	}

	/**
	 * Returns the {@link Instances} from the intersection of the prediction
	 * instance and the expected elements from the ARFF headers.
	 * 
	 * The {@link AcuityScore} cannot have missing values; if it does, then a
	 * null {@link Instances} is returned.
	 * 
	 * A preempted process means the score is high enough to simply throw the
	 * alert. As such a null {@link Instances} is returned.
	 */
	@Override
	public Instances getInstances(List<String> arffHeaderList) {
		if (instance.getAcuityScore().hasMissingValues() || instance.preemptProcess()) {
			return null;
		}
		return InstancesBuilder.build(instance, arffHeaderList);
	}

}
