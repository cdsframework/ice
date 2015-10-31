/**
 * Copyright 2011 OpenCDS.org
 *	Licensed under the Apache License, Version 2.0 (the "License");
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

package org.opencds.vmr.v1_0.internal.utilities;

/**
 * Used to support logic processing within Drools Guvnor environment.
 * @author  Ken Kawamoto
 * @version
 */
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import org.opencds.common.utilities.AbsoluteTimeDifference;
import org.opencds.common.utilities.DateUtility;
import org.opencds.vmr.v1_0.internal.Assertion;
import org.opencds.vmr.v1_0.internal.ClinicalStatement;
import org.opencds.vmr.v1_0.internal.concepts.VmrOpenCdsConcept;

/**
 * @author David Shields
 * 
 * @date
 *
 */
/**
 * @author David Shields
 * 
 * @date
 *
 */
public class LogicHelperUtility
{
  private static LogicHelperUtility instance = new LogicHelperUtility();  //singleton instance
  
  private static DateUtility myDateUtility = DateUtility.getInstance();

  private LogicHelperUtility()
  {
  }

  public static LogicHelperUtility getInstance()
  {
    return instance;
  }
 //TODO add support for concepts from external xml
  
  public static List<String> getConceptTargetIds(List<VmrOpenCdsConcept> openCdsConcepts)
  {
	  ArrayList<String> conceptTargetIds = new ArrayList<String>();
	  for (VmrOpenCdsConcept openCdsConcept : openCdsConcepts)
	  {
		  conceptTargetIds.add(openCdsConcept.getConceptTargetId());
	  }
	  
	  return conceptTargetIds;
  }
  
  public static boolean mapContainsKey(Map<?, ?> map, Object key)
  {
	  return map.containsKey(key);
  }

  /**
   * Without modifying contents of original list, returns new list which only contains ClinicalStatements 
   * without overlapping subject effective times.
   * 
   * To ensure consistent results regardless of ordering in original list, first sorts by subjectEffectiveTimeStart 
   * and only adds clinical statements that don't have a subject effective time that overlaps with clinical
   * statements that have already been added. 
   * 
   * If null provided as input, returns empty list.
   * 
   * @param clinicalStatements_unchangedByMethod
   * @return
   */
  public static List<ClinicalStatement> getClinicalStatementsWithNonOverlappingSubjectEffectiveTime(List<ClinicalStatement> clinicalStatements_unchangedByMethod)
  {
	  List<ClinicalStatement> listToReturn = new ArrayList<ClinicalStatement>();
	  if (clinicalStatements_unchangedByMethod == null)
	  {
		  return listToReturn;
	  }
	  
//	  ClinicalStatementSubjectEffectiveTimeBeginComparator comparator = new ClinicalStatementSubjectEffectiveTimeBeginComparator();
	  
	  List<ClinicalStatement> sortedClinicalStatementList = new ArrayList<ClinicalStatement>(clinicalStatements_unchangedByMethod);
//TODO	  Collections.sort(sortedClinicalStatementList, comparator);
	  
	  // TODO: optimize at some point if keep method; this is implemented in a pretty inefficient manner
	  for (@SuppressWarnings("unused") ClinicalStatement cs : sortedClinicalStatementList)
	  {
		  // subjectEffectiveTime is no longer an attribute of ClinicalStatement, but of the leaf classes, need to re-conceive this method
//		  Date csSubjectEffectiveTimeBegin = cs.getSubjectEffectiveTimeBegin();
//		  Date csSubjectEffectiveTimeEnd = cs.getSubjectEffectiveTimeEnd();
//		  
//		  boolean thisCsOverlapsWithPreviousCs = false;
//		  
//		  for (ClinicalStatement previousCs : listToReturn)
//		  {
//			  Date previousCsSubjectEffectiveTimeBegin = previousCs.getSubjectEffectiveTimeBegin();
//			  Date previousCsSubjectEffectiveTimeEnd = previousCs.getSubjectEffectiveTimeEnd();
//			  
//			  thisCsOverlapsWithPreviousCs = DateUtility.timeIntervalsOverlap(csSubjectEffectiveTimeBegin, csSubjectEffectiveTimeEnd, previousCsSubjectEffectiveTimeBegin, previousCsSubjectEffectiveTimeEnd);
//		  }
//		  if (! thisCsOverlapsWithPreviousCs)
//		  {
//			  listToReturn.add(cs);
//		  }

	  }	  
	  return listToReturn;
  }
  
  /**
   * Returns list of Assertions that has specified value, among assertions provided.  Returns empty list if no match found.
   * @param sourceAssertions_unchanged
   * @param targetValue
   * @return
   */
  public static List<Assertion> getAssertionsWithValue(List<Assertion> sourceAssertions_unchanged, String targetValue)
  {
	 List<Assertion> listToReturn = new ArrayList<Assertion>();
	 
	 if ((sourceAssertions_unchanged != null) && (targetValue != null))
	 {
		 for (Assertion assertion : sourceAssertions_unchanged)
		 {
			 if (assertion.getValue().equals(targetValue))
			 {
				 listToReturn.add(assertion);
			 }
		 }
	 }
	 return listToReturn;
  }
  
  /**
   * Deprecated
   * Creates EvaluatedPerson, which takes the following form:
   * 	EvaluatedPerson
   * 		id = id as specified
   * 		
   * 		ObservationEvent
   * 			code = quality measure
   * 
   * 			RelatedObservation
   * 				sourceRelationshipToTarget = Contains
   * 				code = denominator criteria met
   * 				boolean value = as specified
   * 
   * 			RelatedObservation
   * 				sourceRelationshipToTarget = Contains
   * 				code = numerator criteria met
   * 				boolean value = as specified
   *    
   * 			RelatedEntity
   * 				roleCode = quality measure
   * 				id = id of quality measure as specified
   * 
   * @param flatEvaluatedPersonId
   * @param flatQualityMeasureId
   * @param denominatorCriteriaMet
   * @param numeratorCriteriaMet
   * @return
 * @throws DSSRuntimeExceptionFault 
   */
  /*
  public static org.opencds.vmr.v1_0.schema.Person getExternalEvaluatedPersonForQualityMeasure(
		  org.opencds.vmr.v1_0.internal.datatypes.II 	flatEvaluatedPersonId, 
		  org.opencds.vmr.v1_0.internal.datatypes.II 	flatQualityMeasureId, 
		  boolean 										denominatorCriteriaMet, 
		  boolean 										numeratorCriteriaMet) throws DSSRuntimeExceptionFault
  {
//	  SimpleTerminologyManager str = SimpleTerminologyManager.getInstance();
	  
	  org.opencds.vmr.v1_0.schema.Person person = new org.opencds.vmr.v1_0.schema.Person();
//	  TODO following code needs to be reworked for vmr 1.0  des 20111001
//	  org.opencds.vmr.v1_0.schema.EvaluatedPerson evaluatedPerson = new org.opencds.vmr.v1_0.schema.EvaluatedPerson();
//	  evaluatedPerson.setId(mu.iIInternal2II(flatEvaluatedPersonId));
//	  // TODO: populate variables to make resulting output schema-compliant
//	  
//	  //org.opencds.vmr.v1_0.schema.Person.ClinicalStatements css = new org.opencds.vmr.v1_0.schema.Person.ClinicalStatements();
//	  //externalPerson.setClinicalStatements(css);
//	  
//	  //org.opencds.vmr.v1_0.schema.Person.ClinicalStatements.ObservationEvents oes = new org.opencds.vmr.v1_0.schema.Person.ClinicalStatements.ObservationEvents();
//	  //css.setObservationEvents(oes);
//	  
//	  // create parent observation event
//	  org.opencds.vmr.v1_0.schema.ObservationResult parentOe = new org.opencds.vmr.v1_0.schema.ObservationResult();
//	  
//	  org.opencds.vmr.v1_0.schema.II parentOeII = new org.opencds.vmr.v1_0.schema.II();
//	  parentOeII.setRoot(java.util.UUID.randomUUID().toString());
//	  parentOeII.setExtension("");
//	  parentOe.setId(parentOeII);
//	  
//	  org.opencds.vmr.v1_0.schema.CD parentOeCD = new org.opencds.vmr.v1_0.schema.CD();
//	  parentOeCD.setCodeSystem(CodeSystems.CODE_SYSTEM_OID_OPEN_CDS);
//	  parentOeCD.setCodeSystemName(str.getCodeSystemFromOID(CodeSystems.CODE_SYSTEM_OID_OPEN_CDS));
//	  parentOeCD.setCode("C53");
//	  parentOeCD.setDisplayName("Quality measure");
//	  parentOe.setObservationFocus(parentOeCD);
//	  
//	  // create denominator observation event
//	  org.opencds.vmr.v1_0.schema.ObservationResult denomOe = new org.opencds.vmr.v1_0.schema.ObservationResult();
//	  
//	  org.opencds.vmr.v1_0.schema.II denomOeII = new org.opencds.vmr.v1_0.schema.II();
//	  denomOeII.setRoot(java.util.UUID.randomUUID().toString());
//	  denomOeII.setExtension("");
//	  denomOe.setId(denomOeII);
//	  
//	  org.opencds.vmr.v1_0.schema.CD denomOeCD = new org.opencds.vmr.v1_0.schema.CD();
//	  denomOeCD.setCodeSystem(CodeSystems.CODE_SYSTEM_OID_OPEN_CDS);
//	  denomOeCD.setCodeSystemName(str.getCodeSystemFromOID(CodeSystems.CODE_SYSTEM_OID_OPEN_CDS));
//	  denomOeCD.setCode("C54");
//	  denomOeCD.setDisplayName("Denominator criteria met");
//	  denomOe.setObservationFocus(denomOeCD);
//	  
//	  org.opencds.vmr.v1_0.schema.BL booleanValue = new org.opencds.vmr.v1_0.schema.BL();
//	  booleanValue.setValue(denominatorCriteriaMet);
//	  denomOe.setObservationValue(booleanValue);
//	  
//	  // create numerator observation result
//	  org.opencds.vmr.v1_0.schema.ObservationResult numOe = new org.opencds.vmr.v1_0.schema.ObservationResult();
//	  
//	  org.opencds.vmr.v1_0.schema.II numOeII = new org.opencds.vmr.v1_0.schema.II();
//	  numOeII.setRoot(java.util.UUID.randomUUID().toString());
//	  numOeII.setExtension("");
//	  numOe.setId(numOeII);
//	  
//	  org.opencds.vmr.v1_0.schema.CD numOeCD = new org.opencds.vmr.v1_0.schema.CD();
//	  numOeCD.setCodeSystem(CodeSystems.CODE_SYSTEM_OID_OPEN_CDS);
//	  numOeCD.setCodeSystemName(str.getCodeSystemFromOID(CodeSystems.CODE_SYSTEM_OID_OPEN_CDS));
//	  numOeCD.setCode("C55");
//	  numOeCD.setDisplayName("Numerator criteria met");
//	  numOe.setObservationFocus(numOeCD);
//	  
//	  booleanValue = new org.opencds.vmr.v1_0.schema.BL();
//	  booleanValue.setValue(numeratorCriteriaMet);
//	  numOe.setObservationValue(booleanValue);
//	  
//	  // create quality measure entity
//	  org.opencds.vmr.v1_0.schema.Entity qualityMeasureEntity = new org.opencds.vmr.v1_0.schema.Entity();
//	  qualityMeasureEntity.setId(mu.iIInternal2II(flatQualityMeasureId));
//	  
//	  // associate denom obsRslt to parent obsRslt
////	  org.opencds.vmr.v1_0.schema.ClinicalStatement.RelatedClinicalStatement relatedDenomOe = new org.opencds.vmr.v1_0.schema.ClinicalStatement.RelatedClinicalStatement();
//	  org.opencds.vmr.v1_0.schema.ClinicalStatementRelationship relatedDenomOe = new org.opencds.vmr.v1_0.schema.ClinicalStatementRelationship();
//	  relatedDenomOe.setClinicalStatement(denomOe);
//
//	  //TODO  I think this relationship should be changed from "Contains" to "IsContainedBy"
//	  org.opencds.vmr.v1_0.schema.CD containsCD = new org.opencds.vmr.v1_0.schema.CD();
//	  containsCD.setCodeSystem(CodeSystems.CODE_SYSTEM_OID_OPEN_CDS);
//	  containsCD.setCodeSystemName(str.getCodeSystemFromOID(CodeSystems.CODE_SYSTEM_OID_OPEN_CDS));
//	  containsCD.setCode("C57");
//	  containsCD.setDisplayName("Contains");
//	  relatedDenomOe.setTargetRelationshipToSource(containsCD);
//	  
//	  parentOe.getRelatedClinicalStatement().add(relatedDenomOe);
//
//	  // associate num obsRslt to parent obsRslt
////	  org.opencds.vmr.v1_0.schema.ClinicalStatement.RelatedClinicalStatement relatedNumOe = new org.opencds.vmr.v1_0.schema.ClinicalStatement.RelatedClinicalStatement();
//	  org.opencds.vmr.v1_0.schema.ClinicalStatementRelationship relatedNumOe = new org.opencds.vmr.v1_0.schema.ClinicalStatementRelationship();
//	  relatedNumOe.setClinicalStatement(numOe);
//	  
//	  //TODO  I think this relationship should be changed from "Contains" to "IsContainedBy"
//	  relatedNumOe.setTargetRelationshipToSource(containsCD);
//	  
//	  parentOe.getRelatedClinicalStatement().add(relatedNumOe);
//
//	  
//	  // associate quality measure entity to parent obsRslt
////	  org.opencds.vmr.v1_0.schema.ClinicalStatement.RelatedEntityInRole re = new org.opencds.vmr.v1_0.schema.ClinicalStatement.RelatedEntityInRole();
//	  org.opencds.vmr.v1_0.schema.ClinicalStatementEntityInRoleRelationship re = new org.opencds.vmr.v1_0.schema.ClinicalStatementEntityInRoleRelationship();
//	  re.setEntityInRole(qualityMeasureEntity);
//	  
//	  org.opencds.vmr.v1_0.schema.CD qualityMeasureRoleCD = parentOeCD;
//	  re.setTargetRole(qualityMeasureRoleCD);
//	  
//	  parentOe.getRelatedEntityInRole().add(re);
//	  
//	  // add parent obsRslt to external person
//	  evaluatedPerson.setClinicalStatements(new org.opencds.vmr.v1_0.schema.EvaluatedPerson.ClinicalStatements());
//	  evaluatedPerson.getClinicalStatements().setObservationResults(new org.opencds.vmr.v1_0.schema.EvaluatedPerson.ClinicalStatements.ObservationResults());
////TODO Following line looks good to me, but gives a compile error ???
////	  evaluatedPerson.getClinicalStatements().getObservationResults().getObservationResult().add(parentOe);
//	  
	  return person;  
  }  
  */
 
/*
 * Deprecated
 */
/*
  public static boolean vmrContainsNamedObject(VMR vmr, String objectName)
  {	  
	  //TODO need to fix the contents of VMR to include NamedObjects, I think...  ???
//	  if((vmr == null) || (objectName == null) || (vmr.getNamedObjects() == null))
//	  {
//		  return false;
//	  }
//	  return vmr.getNamedObjects().containsKey(objectName);	  
	  return false;
  }
*/
  
/*
 * Compares a list of conceptTargetId values and times from one VmrOpenCdsConcept to a list of 
 * conceptTargetId values and times from a second VmrOpenCdsConcept and determines the time difference
 * between the minimum time of the list in the first concept and the minimum time of the list in the
 * second concept.  
 * 
 * NOTE:
 * 		1.)Times are calculated by Day, and lesser time elements are ignored. Eleven PM on Monday compared 
 * 			with one AM on Wednesday will return a difference of 2 , even though it is only 26 hours.
 * 
 * 		2.)Return value is in Days.  It will be positive or negative (or zero) depending on whether the 
 * 			first value is less than or greater than the second.  For example, From Monday on the first list 
 * 			to Wednesday on the second list returns 2.  From Wednesday on the first list to Monday on the
 * 			second list returns -2.
 */
  public static long vmrConceptComparisonByTimeInDays(Map<String, java.util.Date> times1, Map<String, java.util.Date> times2)
  //map key=VmrOpenCdsConcept.conceptTargetId, value=java.util.Date of the time we are comparing
  {
	  long daysDifference = 0;
	  int highestReturnedCalendarTimeUnitIsDay = Calendar.DATE;
	  
	  String minimumFirstTimeId = "";
	  for (String oneTimeId : times1.keySet()){
		  //look for minimum time
		  if (("".equals(minimumFirstTimeId) ) || (times1.get(oneTimeId).before(times1.get(minimumFirstTimeId) ) )) {
			  minimumFirstTimeId = oneTimeId;
		  }
	  }
	  
	  String minimumSecondTimeId = "";
	  for (String oneTimeId : times1.keySet()){
		  //first ignore any instances of the same clinical statement in the first set.
		  if (times1.keySet().contains(oneTimeId)) continue;
		  
		  //second eval everything else to find minimum time.
		  if (("".equals(minimumSecondTimeId) ) || (times1.get(oneTimeId).before(times1.get(minimumSecondTimeId) ) )) {
			  minimumSecondTimeId = oneTimeId;
		  }
	  }
	  
	  AbsoluteTimeDifference absoluteTimeDifference = myDateUtility.getAbsoluteTimeDifference(
			  times1.get(minimumFirstTimeId), 
			  times2.get(minimumSecondTimeId), 
			  highestReturnedCalendarTimeUnitIsDay);
	  
	  daysDifference = absoluteTimeDifference.getDayDifference();
	  return daysDifference;
  }
  
  /*
   * Compares a list of conceptTargetId values and times from one VmrOpenCdsConcept to a list of 
   * conceptTargetId values and times from a second VmrOpenCdsConcept and determines the time difference
   * between the minimum time of the list in the first concept and the minimum time of the list in the
   * second concept.  
   * 
   * NOTE:
   * 		1.) Times are calculated by the time unit you request, and lesser time elements may be ignored. 
   * 			If you choose to ignore lesser time units, then 11:00 PM compared 11:59 PM will return a 
   * 			difference of 0, even though it is 59 minutes.
   * 
   * 		2.) Return value is in the time unit you request.  It will be positive or negative (or zero) 
   * 			depending on whether the first value is less than or greater than the second.  For example, 
   * 			asking for HOURS from 9:59 on the first list to 11:00 on the second list returns 2.  The 
   * 			HOURS from 2012-01-01 00:00:00 on the first list to 2011-12-31 22:00:00 on the
   * 			second list returns -1.
   * 
   * 		3.) The returned value will contain the difference in a variety of different units.  Use the
   * 			one that is relevant.
   * 
   * 		4.) You can choose to specify a lower time unit for the level at which to ignore smaller time
   * 			units than the level you specify to return.  See DateUtility and AbsoluteTimeDifference
   * 			modules for details.
   */
    public static AbsoluteTimeDifference vmrConceptComparisonByAbsoluteTimeInTimeUnits(
    		Map<String, java.util.Date> times1, 
    		Map<String, java.util.Date> times2, 
    		int highestReturnedCalendarTimeUnit,
    		boolean ignoreSmallTimeUnits,
    		int highestCalendarTimeUnitToIgnore)
    //map key=VmrOpenCdsConcept.conceptTargetId, value=java.util.Date of the time we are comparing
    {
  	  String minimumFirstTimeId = "";
	  for (String oneTimeId : times1.keySet()){
		  if (("".equals(minimumFirstTimeId) ) || (times1.get(oneTimeId).before(times1.get(minimumFirstTimeId) ) )) {
			  minimumFirstTimeId = oneTimeId;
		  }
	  }
	  
	  String minimumSecondTimeId = "";
	  for (String oneTimeId : times1.keySet()){
		  if (("".equals(minimumSecondTimeId) ) || (times1.get(oneTimeId).before(times1.get(minimumSecondTimeId) ) )) {
			  minimumSecondTimeId = oneTimeId;
		  }
	  }
	  
	  AbsoluteTimeDifference timeDifference = myDateUtility.getAbsoluteTimeDifference(
			 times1.get(minimumFirstTimeId), 
			 times2.get(minimumSecondTimeId), 
			 highestReturnedCalendarTimeUnit,
	    	 ignoreSmallTimeUnits,
	    	 highestCalendarTimeUnitToIgnore);
	  
	  return timeDifference;
    }
  
  public static void main(String [] args)
  {
	  //org.opencds.vmr.v1_0.schema.Person evaluatedPerson = LogicHelperUtility.getExternalEvaluatedPersonForQualityMeasure("personId", "qualityMeasureId", true, false);
	  //System.out.println(evaluatedPerson);
	  //System.out.println();
	  
	  
//	  DateUtility dateUtility = DateUtility.getInstance();
//	  Date evalTime = dateUtility.getDate(2011, 12, 31);
//	  Date comparisonTime = dateUtility.getDate(2009, 12, 31);
//	  System.out.println((dateUtility.getAbsoluteTimeDifference(evalTime, comparisonTime, Calendar.YEAR)).getTimeDifferenceForUnit(Calendar.YEAR));
//	  
//
//	  List procedureEvents = new ArrayList();
//	  ProcedureEvent procedureEvent1 = new ProcedureEvent();
////	  procedureEvent1.setSubjectEffectiveTimeBegin(dateUtility.getDate(2011, 01, 01));
////	  procedureEvent1.setSubjectEffectiveTimeEnd(dateUtility.getDate(2011, 01, 01));
//	  ProcedureEvent procedureEvent2 = new ProcedureEvent();
////	  procedureEvent2.setSubjectEffectiveTimeBegin(dateUtility.getDate(2011, 01, 01));
////	  procedureEvent2.setSubjectEffectiveTimeEnd(dateUtility.getDate(2011, 01, 01));
//	  procedureEvents.add(procedureEvent1);
//	  procedureEvents.add(procedureEvent2);
//	  System.out.println(LogicHelperUtility.getClinicalStatementsWithNonOverlappingSubjectEffectiveTime(procedureEvents).size());
  }
}
