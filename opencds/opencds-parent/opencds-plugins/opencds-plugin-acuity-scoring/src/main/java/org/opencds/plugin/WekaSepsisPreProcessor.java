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

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opencds.plugin.PluginContext.PreProcessPluginContext;
import org.opencds.service.weka.WekaInput;
import org.opencds.vmr.v1_0.internal.CDSInput;
import org.opencds.vmr.v1_0.internal.CDSResource;

public class WekaSepsisPreProcessor implements PreProcessPlugin {
	private static final Log log = LogFactory.getLog(WekaSepsisPreProcessor.class);

    private static final String OPENCDS_CODE_SYSTEM = "2.16.840.1.113883.3.795.12.1.1";
    private static final String ACUITY_SCORE_THRESHOLD_CODE = "ACUITY_SCORE_THRESHOLD";
    private static final String WEKA_THRESHOLD_CODE = "WEKA_THRESHOLD";

	private static final int DEFAULT_ACUITY_SCORE_THRESHOLD = 1000;
	private static final double DEFAULT_WEKA_THRESHOLD = 0.5d;
	private static final String ZIPCODE_PKG_NAME = "zipcode lookup";
	private static final String COMMA = ",";

	private <T> List<T> get(Map<Class<?>, List<?>> allFactLists, Class<T> clazz) {
		List<T> list = (List<T>) allFactLists.get(clazz);
		if (list == null) {
			list = new ArrayList<>();
			allFactLists.put(clazz, list);
		}
		return list;
	}

	@Override
	public void execute(PreProcessPluginContext context) {
		Map<Class<?>, List<?>> allFactLists = context.getAllFactLists();

		List<CDSInput> cdsInputs = (List<CDSInput>) get(allFactLists, CDSInput.class);
		CDSInput cdsInput = null;
		if (cdsInputs != null && cdsInputs.size() > 0) {
			cdsInput = cdsInputs.get(0);
		}

		int acuityScoreThreshold = getAcuityScoreThreshold(cdsInput);
		double wekaThreshold = getWekaThreshold(cdsInput);
		// build the model for WekaInput
		SepsisPredictionInstance instance = SepsisPredictionInstanceBuilder.buildInstance(allFactLists, getZipCodeLookup(context), acuityScoreThreshold, wekaThreshold);

		allFactLists.put(WekaInput.class, Arrays.asList(new SepsisPredictionWekaInput(instance)));

	}

	private int getAcuityScoreThreshold(CDSInput cdsInput) {
        int threshold = DEFAULT_ACUITY_SCORE_THRESHOLD;
        if (cdsInput == null || cdsInput.getCdsResource() == null || cdsInput.getCdsResource().size() == 0) {
            return threshold;
        }
        for (CDSResource cdsr : cdsInput.getCdsResource()) {
            if (OPENCDS_CODE_SYSTEM.equals(cdsr.getCdsResourceType().getCodeSystem())
                    && ACUITY_SCORE_THRESHOLD_CODE.equals(cdsr.getCdsResourceType().getCode())) {
                try {
                    threshold = Integer.valueOf(cdsr.getResourceContents().toString());
                } catch (ClassCastException|NumberFormatException e) {
                    String message = "Error parsing " + ACUITY_SCORE_THRESHOLD_CODE + " resource.  Found "
                            + cdsr.getResourceContents() + "; using default value: "
                            + DEFAULT_ACUITY_SCORE_THRESHOLD;
                    log.error(message, e);
                }
                break;
            }
        }
        log.debug("Using Acurity Score Threshold value: " + threshold);
        return threshold;
	}

	private double getWekaThreshold(CDSInput cdsInput) {
        double threshold = DEFAULT_WEKA_THRESHOLD;
        if (cdsInput == null || cdsInput.getCdsResource() == null || cdsInput.getCdsResource().size() == 0) {
            return threshold;
        }
        for (CDSResource cdsr : cdsInput.getCdsResource()) {
            if (OPENCDS_CODE_SYSTEM.equals(cdsr.getCdsResourceType().getCodeSystem())
                    && WEKA_THRESHOLD_CODE.equals(cdsr.getCdsResourceType().getCode())) {
                try {
                    threshold = Double.valueOf(cdsr.getResourceContents().toString());
                } catch (ClassCastException|NumberFormatException e) {
                    String message = "Error parsing " + WEKA_THRESHOLD_CODE + " resource.  Found "
                            + cdsr.getResourceContents() + "; using default value: " + DEFAULT_WEKA_THRESHOLD;
                    log.error(message, e);
                }
                break;
            }
        }
        return threshold;
	}
	
	private Map<String, String> getZipCodeLookup(PreProcessPluginContext context) {
		PluginDataCache cache = context.getCache();
		Map<String, SupportingData> supportingData = context.getSupportingData();

		SupportingData sd = supportingData.get(ZIPCODE_PKG_NAME);
		Map<String, String> zcLookup = null;
		if (sd != null) {
			zcLookup = cache.get(sd);
			if (zcLookup == null) {
				byte[] data = sd.getSupportingDataPackage().getBytes();
				zcLookup = readLookup(data);
				cache.put(sd, zcLookup);
			}
		}
		return zcLookup;
	}

	private Map<String, String> readLookup(byte[] data) {
		Map<String, String> zcLookup = new HashMap<>();

		try (BufferedReader br = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(data)))) {
			String line;
			int no = 1;
			while ((line = br.readLine()) != null) {
				String[] zipCodeLine = line.split(COMMA);
				if (zipCodeLine.length > 1) {
					try {
						zcLookup.put(zipCodeLine[0], zipCodeLine[1]);
					} catch (Exception e) {
						System.err.println(no + " : " + zipCodeLine[0] + "," + zipCodeLine[1]);
						throw e;
					}
				}
				no++;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		return zcLookup;
	}

}
