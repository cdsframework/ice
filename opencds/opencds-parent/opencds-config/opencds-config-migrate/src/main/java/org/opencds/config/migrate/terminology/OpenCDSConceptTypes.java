/**
 * Copyright 2011 OpenCDS.org
 *	Licensed under the Apache License, Version 2.0 (the "License"", );
 *	you may not use this file except in compliance with the License.
 *	You may obtain a copy of the License at
 *
 *		http://www.apache.org/licenses/LICENSE-2.0
 *
 *	Unless required by applicable law or agreed to in writing, software
 *	distributed under the License is distributed on an "AS IS" BASIS,
 *	WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *	See the License for the specific language governing permissions and
 *	limitations under the License.
 *	
 */

package org.opencds.config.migrate.terminology;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * <p>OpenCDSConceptTypes provides an enumeration of concept types supported within OpenCDS.</p>
 * 
 * @author Kensaku Kawamoto
 * @version 1.00
 */
public class OpenCDSConceptTypes extends Object
{
	// Concept determination method
	public final static String CONCEPT_DETERMINATION_METHOD = "conceptDeterminationMethod";
	
	// Root templates     
	public final static String CDSINPUT_TEMPLATE = "cDSInputTemplate";
	public final static String CDSOUTPUT_TEMPLATE = "cDSOutputTemplate";
	public final static String VMR_TEMPLATE = "vMRTemplate";
    
	// CDSInput 
	public final static String INFORMATION_RECIPIENT_PREFERRED_LANGUAGE = "informationRecipientPreferredLanguage";
	public final static String INFORMATION_RECIPIENT_TYPE = "informationRecipientType";
	public final static String RESOURCE_TYPE = "resourceType";
	public final static String SYSTEM_USER_PREFERRED_LANGUAGE = "systemUserPreferredLanguage";
	public final static String SYSTEM_USER_TASK_CONTEXT = "systemUserTaskContext";
	public final static String SYSTEM_USER_TYPE = "systemUserType";
    
	// Entity
    public final static String ENTITY_TEMPLATE = "entityTemplate";
	public final static String ENTITY_TYPE = "entityType";	
	
	// Demographic data
	public final static String ETHNICITY = "ethnicity";
	public final static String GENDER = "gender";
	public final static String LANGUAGE = "language";
	public final static String RACE = "race";
	
	// Clinical Statement
	public final static String CLINICAL_STATEMENT_TEMPLATE = "clinicalStatementTemplate";
	public final static String DATA_SOURCE_TYPE = "dataSourceType";
	
	// Relationships or Roles
	public final static String CLINICAL_STATEMENT_RELATIONSHIP = "clinicalStatementRelationship";
	public final static String ENTITY_RELATIONSHIP = "entityRelationship";
	public final static String EVALUATED_PERSON_RELATIONSHIP = "evaluatedPersonRelationship";	
//	public final static String ROLE = "role";
	
	// Adverse Event (also applies to Denied Adverse Event)
	public final static String ADVERSE_EVENT_AFFECTED_BODY_SITE = "adverseEventAffectedBodySite";
	public final static String ADVERSE_EVENT_AFFECTED_BODY_SITE_LATERALITY = "adverseEventAffectedBodySiteLaterality";
	public final static String ADVERSE_EVENT = "adverseEvent";
	public final static String ADVERSE_EVENT_AGENT = "adverseEventAgent";
	public final static String ADVERSE_EVENT_CRITICALITY = "adverseEventCriticality";
	public final static String ADVERSE_EVENT_SEVERITY = "adverseEventSeverity";
	public final static String ADVERSE_EVENT_STATUS = "adverseEventStatus";
	
	// Encounter
	public final static String ENCOUNTER_TYPE = "encounterType";
	public final static String ENCOUNTER_CRITICALITY = "encounterCriticality";
	
	// Goal
	public final static String GOAL_CODED_VALUE = "goalCodedValue";
	public final static String GOAL_CRITICALITY = "goalCriticality";
	public final static String GOAL_FOCUS = "goalFocus";
	public final static String GOAL_STATUS = "goalStatus";
	public final static String GOAL_TARGET_BODY_SITE = "goalTargetBodySite";
	public final static String GOAL_TARGET_BODY_SITE_LATERALTIY = "goalTargetBodySiteLaterality";
	
	// Observation (also applies to Denied Observation)
	public final static String OBSERVATION_CODED_VALUE = "observationCodedValue";
	public final static String OBSERVATION_CRITICALITY = "observationCriticality";
	public final static String OBSERVATION_FOCUS = "observationFocus";
	public final static String OBSERVATION_INTERPRETATION = "observationInterpretation";
	public final static String OBSERVATION_METHOD = "observationMethod";
	public final static String OBSERVATION_TARGET_BODY_SITE = "observationTargetBodySite";
	public final static String OBSERVATION_TARGET_BODY_SITE_LATERALITY = "observationTargetBodySiteLaterality";
	public final static String OBSERVATION_UNCONDUCTED_REASON = "observationUnconductedReason";
	
	// Problem (also applies to Denied Problem)
	public final static String PROBLEM = "problem";
	public final static String PROBLEM_AFFECTED_BODY_SITE = "problemAffectedBodySite";
	public final static String PROBLEM_AFFECTED_BODY_SITE_LATERALITY = "problemAffectedBodySiteLaterality";
	public final static String PROBLEM_IMPORTANCE = "problemImportance";
	public final static String PROBLEM_SEVERITY = "problemSeverity";
	public final static String PROBLEM_STATUS = "problemStatus";
		
	// Procedure (also applies to Undelivered Procedure)
	public final static String PROCEDURE = "procedure";
	public final static String PROCEDURE_APPROACH_BODY_SITE = "procedureApproachBodySite";
	public final static String PROCEDURE_APPROACH_BODY_SITE_LATERALITY = "procedureApproachBodySiteLaterality";
	public final static String PROCEDURE_CRITICALITY = "procedureCriticality";
	public final static String PROCEDURE_METHOD = "procedureMethod";
	public final static String PROCEDURE_TARGET_BODY_SITE = "procedureTargetBodySite";
	public final static String PROCEDURE_TARGET_BODY_SITE_LATERALITY = "procedureTargetBodySiteLaterality";
	public final static String PROCEDURE_UNDELIVERED_REASON = "procedureUndeliveredReason";
	
	// Substances
	public final static String BRANDED_MEDICATION = "brandedMedication";
	public final static String DOSE_TYPE = "doseType";
	public final static String DOSING_SIG = "dosingSig";
	public final static String GENERIC_MEDICATION = "genericMedication";
	public final static String IMMUNIZATION = "immunization";
	public final static String INFORMATION_ATTESTATION_TYPE = "informationAttestationType";
	public final static String MANUFACTURER = "manufacturer";
	public final static String MEDICATION = "medication";
	public final static String MEDICATION_CLASS = "medicationClass";
	public final static String SUBSTANCE_FORM = "substanceForm";
	
	// Substance Administration (also applies to Undelivered Substance Administration)
	public final static String SUBSTANCE_ADMINISTRATION_APPROACH_BODY_SITE = "substanceAdministrationApproachBodySite";
	public final static String SUBSTANCE_ADMINISTRATION_APPROACH_BODY_SITE_LATERALITY = "substanceAdministrationApproachBodySiteLaterality";
	public final static String SUBSTANCE_ADMINISTRATION_CRITICALITY = "substanceAdministrationCriticality";
	public final static String SUBSTANCE_ADMINISTRATION_GENERAL_PURPOSE = "substanceAdministrationGeneralPurpose";
	public final static String SUBSTANCE_DELIVERY_METHOD = "substanceDeliveryMethod";
	public final static String SUBSTANCE_DELIVERY_ROUTE = "substanceDeliveryRoute";
	public final static String SUBSTANCE_ADMINISTRATION_TARGET_BODY_SITE = "substanceAdministrationTargetBodySite";
	public final static String SUBSTANCE_ADMINISTRATION_TARGET_BODY_SITE_LATERALITY = "substanceAdministrationTargetBodySiteLaterality";
	public final static String SUBSTANCE_ADMINISTRATION_UNDELIVERED_REASON = "substanceAdministrationUndeliveredReason";
	
	// Supply (also applies to Undelivered Supply)
	public final static String SUPPLY = "supply";
	public final static String SUPPLY_CRITICALITY = "supplyCriticality";
	
	
	
	/**
	 * Returns all OpenCDS concept types.
	 * @return 
	 * @return
	 */
	public final static ArrayList<String> getOpenCdsConceptTypes()
	{
		ArrayList<String> arrayToReturn = new ArrayList<String>();
		arrayToReturn.add(CONCEPT_DETERMINATION_METHOD);
		
		// Root templates     
		arrayToReturn.add(CDSINPUT_TEMPLATE);
		arrayToReturn.add(CDSOUTPUT_TEMPLATE);
		arrayToReturn.add(VMR_TEMPLATE);
	    
		// CDSInput 
		arrayToReturn.add(SYSTEM_USER_TYPE);
		arrayToReturn.add(SYSTEM_USER_PREFERRED_LANGUAGE);
		arrayToReturn.add(INFORMATION_RECIPIENT_TYPE);
		arrayToReturn.add(INFORMATION_RECIPIENT_PREFERRED_LANGUAGE);
		arrayToReturn.add(SYSTEM_USER_TASK_CONTEXT);
		arrayToReturn.add(RESOURCE_TYPE);
	    
		// Entity
		arrayToReturn.add(ENTITY_TEMPLATE);		
		arrayToReturn.add(ENTITY_TYPE);
		
		arrayToReturn.add(LANGUAGE);
		arrayToReturn.add(GENDER);
		arrayToReturn.add(ETHNICITY);
		arrayToReturn.add(RACE);
		
		arrayToReturn.add(CLINICAL_STATEMENT_RELATIONSHIP);
		arrayToReturn.add(ENTITY_RELATIONSHIP);
		arrayToReturn.add(EVALUATED_PERSON_RELATIONSHIP);	
//		arrayToReturn.add(ROLE);
		
		arrayToReturn.add(CLINICAL_STATEMENT_TEMPLATE);
		arrayToReturn.add(DATA_SOURCE_TYPE);
		
		arrayToReturn.add(ADVERSE_EVENT_AFFECTED_BODY_SITE);
		arrayToReturn.add(ADVERSE_EVENT_AFFECTED_BODY_SITE_LATERALITY);
		arrayToReturn.add(ADVERSE_EVENT);
		arrayToReturn.add(ADVERSE_EVENT_AGENT);
		arrayToReturn.add(ADVERSE_EVENT_CRITICALITY);
		arrayToReturn.add(ADVERSE_EVENT_STATUS);
		arrayToReturn.add(ADVERSE_EVENT_SEVERITY);
		
		arrayToReturn.add(ENCOUNTER_CRITICALITY);
		arrayToReturn.add(ENCOUNTER_TYPE);
		
		arrayToReturn.add(GOAL_FOCUS);
		arrayToReturn.add(GOAL_CODED_VALUE);
		arrayToReturn.add(GOAL_CRITICALITY);
		arrayToReturn.add(GOAL_STATUS);
		arrayToReturn.add(GOAL_TARGET_BODY_SITE);
		arrayToReturn.add(GOAL_TARGET_BODY_SITE_LATERALTIY);
				
		arrayToReturn.add(OBSERVATION_FOCUS);
		arrayToReturn.add(OBSERVATION_METHOD);
		arrayToReturn.add(OBSERVATION_CODED_VALUE);
		arrayToReturn.add(OBSERVATION_INTERPRETATION);
		arrayToReturn.add(OBSERVATION_CRITICALITY);
		arrayToReturn.add(OBSERVATION_UNCONDUCTED_REASON);
		arrayToReturn.add(OBSERVATION_TARGET_BODY_SITE);
		arrayToReturn.add(OBSERVATION_TARGET_BODY_SITE_LATERALITY);
		
		arrayToReturn.add(PROBLEM_AFFECTED_BODY_SITE);
		arrayToReturn.add(PROBLEM_AFFECTED_BODY_SITE_LATERALITY);
		arrayToReturn.add(PROBLEM);
		arrayToReturn.add(PROBLEM_STATUS);
		arrayToReturn.add(PROBLEM_SEVERITY);
		arrayToReturn.add(PROBLEM_IMPORTANCE);
		
		arrayToReturn.add(PROCEDURE_APPROACH_BODY_SITE);
		arrayToReturn.add(PROCEDURE_APPROACH_BODY_SITE_LATERALITY);
		arrayToReturn.add(PROCEDURE);
		arrayToReturn.add(PROCEDURE_CRITICALITY);
		arrayToReturn.add(PROCEDURE_METHOD);
		arrayToReturn.add(PROCEDURE_UNDELIVERED_REASON);
		arrayToReturn.add(PROCEDURE_TARGET_BODY_SITE);
		arrayToReturn.add(PROCEDURE_TARGET_BODY_SITE_LATERALITY);
		
		arrayToReturn.add(MEDICATION_CLASS);
		arrayToReturn.add(MEDICATION);
		arrayToReturn.add(GENERIC_MEDICATION);
		arrayToReturn.add(BRANDED_MEDICATION);
		arrayToReturn.add(MANUFACTURER);
		arrayToReturn.add(IMMUNIZATION);
		arrayToReturn.add(SUBSTANCE_FORM);		
		
		arrayToReturn.add(SUBSTANCE_ADMINISTRATION_APPROACH_BODY_SITE);
		arrayToReturn.add(SUBSTANCE_ADMINISTRATION_APPROACH_BODY_SITE_LATERALITY);
		arrayToReturn.add(SUBSTANCE_ADMINISTRATION_TARGET_BODY_SITE);
		arrayToReturn.add(SUBSTANCE_ADMINISTRATION_TARGET_BODY_SITE_LATERALITY);
		arrayToReturn.add(SUBSTANCE_DELIVERY_METHOD);
		arrayToReturn.add(SUBSTANCE_DELIVERY_ROUTE);
		arrayToReturn.add(SUBSTANCE_ADMINISTRATION_GENERAL_PURPOSE);
		arrayToReturn.add(DOSE_TYPE);
		arrayToReturn.add(INFORMATION_ATTESTATION_TYPE);
		arrayToReturn.add(DOSING_SIG);
		arrayToReturn.add(SUBSTANCE_ADMINISTRATION_CRITICALITY);
		arrayToReturn.add(SUBSTANCE_ADMINISTRATION_UNDELIVERED_REASON);
		
		arrayToReturn.add(SUPPLY);
		arrayToReturn.add(SUPPLY_CRITICALITY);
		
		return arrayToReturn;		
	}
	public final static void getOpenCdsConceptTypes(HashMap<String, String> myConceptTypesNametoConceptTypeMap)
	{
		myConceptTypesNametoConceptTypeMap.put("CONCEPT_DETERMINATION_METHOD", CONCEPT_DETERMINATION_METHOD);
		
		// Root templates     
		myConceptTypesNametoConceptTypeMap.put("CDSINPUT_TEMPLATE", CDSINPUT_TEMPLATE);
		myConceptTypesNametoConceptTypeMap.put("CDSOUTPUT_TEMPLATE", CDSOUTPUT_TEMPLATE);
		myConceptTypesNametoConceptTypeMap.put("VMR_TEMPLATE", VMR_TEMPLATE);
	    
		// CDSInput 
		myConceptTypesNametoConceptTypeMap.put("SYSTEM_USER_TYPE", SYSTEM_USER_TYPE);
		myConceptTypesNametoConceptTypeMap.put("SYSTEM_USER_PREFERRED_LANGUAGE", SYSTEM_USER_PREFERRED_LANGUAGE);
		myConceptTypesNametoConceptTypeMap.put("INFORMATION_RECIPIENT_TYPE", INFORMATION_RECIPIENT_TYPE);
		myConceptTypesNametoConceptTypeMap.put("INFORMATION_RECIPIENT_PREFERRED_LANGUAGE", INFORMATION_RECIPIENT_PREFERRED_LANGUAGE);
		myConceptTypesNametoConceptTypeMap.put("SYSTEM_USER_TASK_CONTEXT", SYSTEM_USER_TASK_CONTEXT);
		myConceptTypesNametoConceptTypeMap.put("RESOURCE_TYPE", RESOURCE_TYPE);
	    
		// Entity
		myConceptTypesNametoConceptTypeMap.put("ENTITY_TEMPLATE", ENTITY_TEMPLATE);		
		myConceptTypesNametoConceptTypeMap.put("ENTITY_TYPE", ENTITY_TYPE);
		
		myConceptTypesNametoConceptTypeMap.put("LANGUAGE", LANGUAGE);
		myConceptTypesNametoConceptTypeMap.put("GENDER", GENDER);
		myConceptTypesNametoConceptTypeMap.put("ETHNICITY", ETHNICITY);
		myConceptTypesNametoConceptTypeMap.put("RACE", RACE);
		
		myConceptTypesNametoConceptTypeMap.put("CLINICAL_STATEMENT_RELATIONSHIP", CLINICAL_STATEMENT_RELATIONSHIP);
		myConceptTypesNametoConceptTypeMap.put("ENTITY_RELATIONSHIP", ENTITY_RELATIONSHIP);
		myConceptTypesNametoConceptTypeMap.put("EVALUATED_PERSON_RELATIONSHIP", EVALUATED_PERSON_RELATIONSHIP);
//		myConceptTypesNametoConceptTypeMap.put("ROLE", ROLE);
		
		myConceptTypesNametoConceptTypeMap.put("CLINICAL_STATEMENT_TEMPLATE", CLINICAL_STATEMENT_TEMPLATE);
		myConceptTypesNametoConceptTypeMap.put("DATA_SOURCE_TYPE", DATA_SOURCE_TYPE);
		
		myConceptTypesNametoConceptTypeMap.put("ADVERSE_EVENT_AFFECTED_BODY_SITE", ADVERSE_EVENT_AFFECTED_BODY_SITE);
		myConceptTypesNametoConceptTypeMap.put("ADVERSE_EVENT_AFFECTED_BODY_SITE_LATERALITY", ADVERSE_EVENT_AFFECTED_BODY_SITE_LATERALITY);
		myConceptTypesNametoConceptTypeMap.put("ADVERSE_EVENT", ADVERSE_EVENT);
		myConceptTypesNametoConceptTypeMap.put("ADVERSE_EVENT_AGENT", ADVERSE_EVENT_AGENT);
		myConceptTypesNametoConceptTypeMap.put("ADVERSE_EVENT_CRITICALITY", ADVERSE_EVENT_CRITICALITY);
		myConceptTypesNametoConceptTypeMap.put("ADVERSE_EVENT_STATUS", ADVERSE_EVENT_STATUS);
		myConceptTypesNametoConceptTypeMap.put("ADVERSE_EVENT_SEVERITY", ADVERSE_EVENT_SEVERITY);
		
		myConceptTypesNametoConceptTypeMap.put("ENCOUNTER_CRITICALITY", ENCOUNTER_CRITICALITY);
		myConceptTypesNametoConceptTypeMap.put("ENCOUNTER_TYPE", ENCOUNTER_TYPE);
		
		myConceptTypesNametoConceptTypeMap.put("GOAL_FOCUS", GOAL_FOCUS);
		myConceptTypesNametoConceptTypeMap.put("GOAL_CODED_VALUE", GOAL_CODED_VALUE);
		myConceptTypesNametoConceptTypeMap.put("GOAL_CRITICALITY", GOAL_CRITICALITY);
		myConceptTypesNametoConceptTypeMap.put("GOAL_STATUS", GOAL_STATUS);
		myConceptTypesNametoConceptTypeMap.put("GOAL_TARGET_BODY_SITE", GOAL_TARGET_BODY_SITE);
		myConceptTypesNametoConceptTypeMap.put("GOAL_TARGET_BODY_SITE_LATERALTIY", GOAL_TARGET_BODY_SITE_LATERALTIY);
				
		myConceptTypesNametoConceptTypeMap.put("OBSERVATION_FOCUS", OBSERVATION_FOCUS);
		myConceptTypesNametoConceptTypeMap.put("OBSERVATION_METHOD", OBSERVATION_METHOD);
		myConceptTypesNametoConceptTypeMap.put("OBSERVATION_CODED_VALUE", OBSERVATION_METHOD);
		myConceptTypesNametoConceptTypeMap.put("OBSERVATION_INTERPRETATION", OBSERVATION_INTERPRETATION);
		myConceptTypesNametoConceptTypeMap.put("OBSERVATION_CRITICALITY", OBSERVATION_CRITICALITY);
		myConceptTypesNametoConceptTypeMap.put("OBSERVATION_UNCONDUCTED_REASON", OBSERVATION_UNCONDUCTED_REASON);
		myConceptTypesNametoConceptTypeMap.put("OBSERVATION_TARGET_BODY_SITE", OBSERVATION_TARGET_BODY_SITE);
		myConceptTypesNametoConceptTypeMap.put("OBSERVATION_TARGET_BODY_SITE_LATERALITY", OBSERVATION_TARGET_BODY_SITE_LATERALITY);
		
		myConceptTypesNametoConceptTypeMap.put("PROBLEM_AFFECTED_BODY_SITE", PROBLEM_AFFECTED_BODY_SITE);
		myConceptTypesNametoConceptTypeMap.put("PROBLEM_AFFECTED_BODY_SITE_LATERALITY", PROBLEM_AFFECTED_BODY_SITE_LATERALITY);
		myConceptTypesNametoConceptTypeMap.put("PROBLEM", PROBLEM);
		myConceptTypesNametoConceptTypeMap.put("PROBLEM_STATUS", PROBLEM_STATUS);
		myConceptTypesNametoConceptTypeMap.put("PROBLEM_SEVERITY", PROBLEM_SEVERITY);
		myConceptTypesNametoConceptTypeMap.put("PROBLEM_IMPORTANCE", PROBLEM_IMPORTANCE);
		
		myConceptTypesNametoConceptTypeMap.put("PROCEDURE_APPROACH_BODY_SITE", PROCEDURE_APPROACH_BODY_SITE);
		myConceptTypesNametoConceptTypeMap.put("PROCEDURE_APPROACH_BODY_SITE_LATERALITY", PROCEDURE_APPROACH_BODY_SITE_LATERALITY);
		myConceptTypesNametoConceptTypeMap.put("PROCEDURE", PROCEDURE);
		myConceptTypesNametoConceptTypeMap.put("PROCEDURE_CRITICALITY", PROCEDURE_CRITICALITY);
		myConceptTypesNametoConceptTypeMap.put("PROCEDURE_METHOD", PROCEDURE_METHOD);
		myConceptTypesNametoConceptTypeMap.put("PROCEDURE_UNDELIVERED_REASON", PROCEDURE_UNDELIVERED_REASON);
		myConceptTypesNametoConceptTypeMap.put("PROCEDURE_TARGET_BODY_SITE", PROCEDURE_TARGET_BODY_SITE);
		myConceptTypesNametoConceptTypeMap.put("PROCEDURE_TARGET_BODY_SITE_LATERALITY", PROCEDURE_TARGET_BODY_SITE_LATERALITY);
		
		myConceptTypesNametoConceptTypeMap.put("MEDICATION_CLASS", MEDICATION_CLASS);
		myConceptTypesNametoConceptTypeMap.put("MEDICATION", MEDICATION);
		myConceptTypesNametoConceptTypeMap.put("GENERIC_MEDICATION", GENERIC_MEDICATION);
		myConceptTypesNametoConceptTypeMap.put("BRANDED_MEDICATION", BRANDED_MEDICATION);
		myConceptTypesNametoConceptTypeMap.put("IMMUNIZATION", IMMUNIZATION);		
		myConceptTypesNametoConceptTypeMap.put("MANUFACTURER", MANUFACTURER);
		myConceptTypesNametoConceptTypeMap.put("SUBSTANCE_FORM", SUBSTANCE_FORM);
		
		myConceptTypesNametoConceptTypeMap.put("SUBSTANCE_ADMINISTRATION_APPROACH_BODY_SITE", SUBSTANCE_ADMINISTRATION_APPROACH_BODY_SITE);
		myConceptTypesNametoConceptTypeMap.put("SUBSTANCE_ADMINISTRATION_APPROACH_BODY_SITE_LATERALITY", SUBSTANCE_ADMINISTRATION_APPROACH_BODY_SITE_LATERALITY);
		myConceptTypesNametoConceptTypeMap.put("SUBSTANCE_ADMINISTRATION_TARGET_BODY_SITE", SUBSTANCE_ADMINISTRATION_TARGET_BODY_SITE);
		myConceptTypesNametoConceptTypeMap.put("SUBSTANCE_ADMINISTRATION_TARGET_BODY_SITE_LATERALITY", SUBSTANCE_ADMINISTRATION_TARGET_BODY_SITE_LATERALITY);
		myConceptTypesNametoConceptTypeMap.put("SUBSTANCE_DELIVERY_METHOD", SUBSTANCE_DELIVERY_METHOD);
		myConceptTypesNametoConceptTypeMap.put("SUBSTANCE_DELIVERY_ROUTE", SUBSTANCE_DELIVERY_ROUTE);
		myConceptTypesNametoConceptTypeMap.put("SUBSTANCE_ADMINISTRATION_GENERAL_PURPOSE", SUBSTANCE_ADMINISTRATION_GENERAL_PURPOSE);
		myConceptTypesNametoConceptTypeMap.put("DOSE_TYPE", DOSE_TYPE);
		myConceptTypesNametoConceptTypeMap.put("INFORMATION_ATTESTATION_TYPE", INFORMATION_ATTESTATION_TYPE);
		myConceptTypesNametoConceptTypeMap.put("DOSING_SIG", DOSING_SIG);
		myConceptTypesNametoConceptTypeMap.put("SUBSTANCE_ADMINISTRATION_CRITICALITY", SUBSTANCE_ADMINISTRATION_CRITICALITY);
		myConceptTypesNametoConceptTypeMap.put("SUBSTANCE_ADMINISTRATION_UNDELIVERED_REASON", SUBSTANCE_ADMINISTRATION_UNDELIVERED_REASON);
		
		myConceptTypesNametoConceptTypeMap.put("SUPPLY", SUPPLY);
		myConceptTypesNametoConceptTypeMap.put("SUPPLY_CRITICALITY", SUPPLY_CRITICALITY);
		
		return;		
	}
}