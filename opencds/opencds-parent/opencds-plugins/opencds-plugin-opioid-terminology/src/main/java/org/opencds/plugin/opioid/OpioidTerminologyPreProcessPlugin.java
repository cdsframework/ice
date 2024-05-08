/*
 * Copyright 2020 OpenCDS.org
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

package org.opencds.plugin.opioid;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opencds.plugin.PluginContext.PreProcessPluginContext;
import org.opencds.plugin.PluginDataCache;
import org.opencds.plugin.PreProcessPlugin;
import org.opencds.plugin.SupportingData;
import org.opencds.plugin.opioid.query.*;
import org.opencds.plugin.opioid.util.AccessConnection;

import java.io.File;
import java.util.*;

public class OpioidTerminologyPreProcessPlugin implements PreProcessPlugin {
	private static final Log log = LogFactory.getLog(OpioidTerminologyPreProcessPlugin.class);
	private static final String ACCESS_DB_ID = "OPIOID_DB";

	private static final String A1 = "myDrugRxCuiSetForOpioids";
	private static final String A2 = "myDrugRxCuiSetForOpioidsAbusedInPrimaryCare";
	private static final String A3 = "myDrugRxCuiSetForOpioidsCodeineCoughMed";
	private static final String A4 = "myDrugRxCuiSetForOpioidsIndicatingEndOfLife";
	private static final String B1 = "myDrugRxCuiSetForOpioids_Str";
	private static final String B2 = "myDrugRxCuiSetForOpioidsAbusedInPrimaryCare_Str";
	private static final String B3 = "myDrugRxCuiSetForOpioidsCodeineCoughMed_Str";
	private static final String B4 = "myDrugRxCuiSetForOpioidsIndicatingEndOfLife_Str";
	private static final String RX_CUI_OPIOIDS = "SELECT DRUG_RXCUI FROM OPIOID";
	private static final String RX_CUI_OPIOIDS_ABUSE = "SELECT DRUG_RXCUI FROM OPIOID_ABUSED_IN_PRIMARY_CARE";
	private static final String RX_CUI_OPIOIDS_CODEINE_COUGH_MED = "SELECT DRUG_RXCUI FROM OPIOID_CODEINE_COUGH_MED";
	private static final String RX_CUI_OPIOIDS_EOL = "SELECT DRUG_RXCUI FROM OPIOID_INDICATING_END_OF_LIFE";
	
	private static final String C = "myRxCuiToNameMap";
	private static final Set<String> RX_CUI_TO_NAME_QUERIES = new HashSet<>(Arrays.asList(
			"SELECT INGREDIENT_RXCUI, INGREDIENT_NAME FROM MED_INGREDIENT",
			"SELECT DRUG_RXCUI, DRUG_NAME FROM MED_DRUG",
			"SELECT DOSE_FORM_RXCUI, DOSE_FORM_NAME FROM MED_DOSE_FORM",
			"SELECT DOSE_FORM_GROUP_RXCUI, DOSE_FORM_GROUP_NAME FROM MED_DOSE_FORM_GROUP",
			"SELECT SCDC_RXCUI, SCDC_NAME FROM MED_SCDC"
			));
	
	private static final String D = "myDrugRxCuiToDoseFormRxCuiMap";
	private static final String RX_CUI_DOSE_FORM = "SELECT DISTINCT MED_DRUG_DOSE_FORM.DRUG_RXCUI, MED_DRUG_DOSE_FORM.DOSE_FORM_RXCUI FROM MED_DRUG_DOSE_FORM";
	
	private static final String E = "myDrugRxCuiToDoseFormGroupRxCuiSetMap";
	private static final String RX_CUI_DOSE_FORM_GROUP = "SELECT DISTINCT MED_DRUG_DOSE_FORM.DRUG_RXCUI, MED_DRUG_DOSE_FORM.DOSE_FORM_RXCUI FROM MED_DRUG_DOSE_FORM";

	private static final String F = "myOpioidAbusedInPrimaryCareDrugRxCuiToScdcRxCuiSetContainingOpioidSetMap";

	private static final String F_QUERY = "SELECT DISTINCT MED_SCDC_FOR_DRUG.DRUG_RXCUI, MED_SCDC_FOR_DRUG.SCDC_RXCUI "
			+ " FROM MED_INGREDIENT_FOR_SCDC "
			+ " INNER JOIN MED_SCDC_FOR_DRUG "
			+ "    ON MED_SCDC_FOR_DRUG.SCDC_RXCUI = MED_INGREDIENT_FOR_SCDC.SCDC_RXCUI "
			+ " WHERE MED_INGREDIENT_FOR_SCDC.INGREDIENT_RXCUI IN "
			+ " ( "
			+ " 	SELECT DISTINCT INGREDIENT_RXCUI FROM MED_INGREDIENT_TYPE WHERE INGREDIENT_TYPE = 'Opioid' "
			+ " ) "
			+ "    AND MED_SCDC_FOR_DRUG.DRUG_RXCUI IN (SELECT DISTINCT DRUG_RXCUI FROM OPIOID_ABUSED_IN_PRIMARY_CARE)";
			

	private static final String G = "myOpioidAbusedInPrimaryCareScdcRxCuiToOpioidIngredientRxCuiSetMap";
	private static final String G_QUERY = "SELECT DISTINCT MED_INGREDIENT_FOR_SCDC.SCDC_RXCUI, MED_INGREDIENT_FOR_SCDC.INGREDIENT_RXCUI "
			+ " FROM MED_INGREDIENT_FOR_SCDC "
			+ " WHERE MED_INGREDIENT_FOR_SCDC.SCDC_RXCUI IN "
			+ " (  SELECT DISTINCT MED_SCDC_FOR_DRUG.SCDC_RXCUI "
			+ "    FROM MED_INGREDIENT_FOR_SCDC "
			+ "    INNER JOIN MED_SCDC_FOR_DRUG "
			+ "      ON MED_SCDC_FOR_DRUG.SCDC_RXCUI = MED_INGREDIENT_FOR_SCDC.SCDC_RXCUI "
			+ "    WHERE MED_INGREDIENT_FOR_SCDC.INGREDIENT_RXCUI IN "
			+ "    ( "
			+ " 	  SELECT DISTINCT INGREDIENT_RXCUI FROM MED_INGREDIENT_TYPE WHERE INGREDIENT_TYPE = 'Opioid' "
			+ "    ) "
			+ "    AND MED_SCDC_FOR_DRUG.DRUG_RXCUI IN (SELECT DISTINCT DRUG_RXCUI FROM OPIOID_ABUSED_IN_PRIMARY_CARE)"
			+ "    AND MED_INGREDIENT_FOR_SCDC.INGREDIENT_RXCUI IN "
			+ "    ( "
			+ "       SELECT DISTINCT INGREDIENT_RXCUI FROM MED_INGREDIENT_TYPE WHERE INGREDIENT_TYPE = 'Opioid' "
			+ "    ) "
			+ " )";

	private static final String H = "myScdcRxCuiToStrengthValueMap";
	private static final String H_QUERY = "SELECT DISTINCT MED_SCDC.SCDC_RXCUI, MED_SCDC.STRENGTH_VALUE FROM MED_SCDC";
	
	private static final String I = "myScdcRxCuiToStrengthUnitMap";
	private static final String I_QUERY = "SELECT DISTINCT MED_SCDC.SCDC_RXCUI, MED_SCDC.STRENGTH_UNIT FROM MED_SCDC";
	
	private static final String J = "myConditionsIndicatingEndOfLifeCodeSet_ICD10CM";
	private static final String J_QUERY = "SELECT DISTINCT MEMBER_CODE FROM VALUE_SET_MEMBER WHERE VALUE_SET_ID = 1 AND MEMBER_UMLS_SAB = 'ICD10CM'";
	
	private static final String K = "myConditionsIndicatingEndOfLifeCodeSet_SNOMEDCT_US";
	private static final String K_QUERY = "SELECT DISTINCT MEMBER_CODE FROM VALUE_SET_MEMBER WHERE VALUE_SET_ID = 1 AND MEMBER_UMLS_SAB = 'SNOMEDCT_US'";
	
	private static final String L = "myOpiateUrineDrugTestCodeSet_LNC";
	private static final String L_QUERY = "SELECT DISTINCT MEMBER_CODE FROM VALUE_SET_MEMBER WHERE VALUE_SET_ID = 2 AND MEMBER_UMLS_SAB = 'LNC'";
	
	@Override
	public void execute(PreProcessPluginContext context) {
		log.debug(getClass().getSimpleName() + ": Processing input data.");
		Map<String, SupportingData> sdMap = context.getSupportingData();
		SupportingData sd = sdMap.get(ACCESS_DB_ID);
		log.debug("SD: " + sd);
		if (sd != null) {
			context.getGlobals().put("opioidGlobals", new HashMap<>(getGlobals(context.getCache(), sd)));
		}
	}

	private Map<String, Object> getGlobals(PluginDataCache pluginDataCache, SupportingData sd) {
		Map<String, Object> cachedGlobals = pluginDataCache.get(sd);
		if (cachedGlobals == null) {
			File accdbFile = getAccessFile(sd);
			try (AccessConnection conn = new AccessConnection(accdbFile)) {
				cachedGlobals = new HashMap<>();
				cachedGlobals.put(A1, RxCuiInclusionsInt.query(conn, RX_CUI_OPIOIDS));
				cachedGlobals.put(A2, RxCuiInclusionsInt.query(conn, RX_CUI_OPIOIDS_ABUSE));
				cachedGlobals.put(A3, RxCuiInclusionsInt.query(conn, RX_CUI_OPIOIDS_CODEINE_COUGH_MED));
				cachedGlobals.put(A4, RxCuiInclusionsInt.query(conn, RX_CUI_OPIOIDS_EOL));
				cachedGlobals.put(B1, RxCuiInclusionsString.query(conn, RX_CUI_OPIOIDS));
				cachedGlobals.put(B2, RxCuiInclusionsString.query(conn, RX_CUI_OPIOIDS_ABUSE));
				cachedGlobals.put(B3, RxCuiInclusionsString.query(conn, RX_CUI_OPIOIDS_CODEINE_COUGH_MED));
				cachedGlobals.put(B4, RxCuiInclusionsString.query(conn, RX_CUI_OPIOIDS_EOL));
				cachedGlobals.put(C, RxCuiToName.query(conn, RX_CUI_TO_NAME_QUERIES));
				cachedGlobals.put(D, RxCuiToDoseForm.query(conn, RX_CUI_DOSE_FORM));
				cachedGlobals.put(E, RxCuiGroupings.query(conn, RX_CUI_DOSE_FORM_GROUP));
				cachedGlobals.put(F, RxCuiGroupings.query(conn, F_QUERY));
				cachedGlobals.put(G, RxCuiGroupings.query(conn, G_QUERY));
				cachedGlobals.put(H, RxCuiToStrengthValue.query(conn, H_QUERY));
				cachedGlobals.put(I, RxCuiToStrengthUnit.query(conn, I_QUERY));
				cachedGlobals.put(J, UmlsMemberConceptInclusionsString.query(conn, J_QUERY));
				cachedGlobals.put(K, UmlsMemberConceptInclusionsString.query(conn, K_QUERY));
				cachedGlobals.put(L, UmlsMemberConceptInclusionsString.query(conn, L_QUERY));
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
		return cachedGlobals;
	}

	private File getAccessFile(SupportingData sd) {
		return sd == null ? null : sd.getSupportingDataPackage().getFile();
	}
}
