/**
 * 
 */
package org.opencds.support.guvnor;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;

import org.opencds.common.exceptions.OpenCDSRuntimeException;
import org.opencds.common.utilities.DateUtility;
import org.opencds.vmr.v1_0.internal.ClinicalStatementRelationship;
import org.opencds.vmr.v1_0.internal.concepts.EncounterTypeConcept;
import org.opencds.vmr.v1_0.internal.concepts.ProcedureConcept;

/**
 * @author David Shields
 *
 */
public class GuvnorFunctionWorkup {
	@SuppressWarnings({ "unchecked", "rawtypes" })
	java.util.List getEncounterConceptListToInsert(List procedureConcepts, List clinicalStatementRelationships, List encounterConcepts) 
{
/*
	Returns list of encounterTypeConcepts.
	Returns an empty list if either procedureConcepts or clinicalStatementRelationships is null or empty.
*/
	
    java.util.Date beginTime = new java.util.Date();
    String beginDate = DateUtility.getInstance().getDateAsString(new java.util.Date(), "yyyyMMddHHmmss.SSSZZZZZ");
		
	Set<String> etcSet = new HashSet<String>();
	Set<String> pcSet = new HashSet<String>();
	java.util.List<EncounterTypeConcept> listToReturn = new java.util.ArrayList<EncounterTypeConcept>();
	
	if ((procedureConcepts == null) || (procedureConcepts.size() == 0))
	{
		System.out.println(beginDate + " procedureConcepts=null or empty" );
		return listToReturn;
	}
	
	if ((clinicalStatementRelationships == null) || (clinicalStatementRelationships.size() == 0))
	{
		System.out.println(beginDate + " clinicalStatementRelationships=null or empty" );
		return listToReturn;
	}
	
	if (encounterConcepts == null)
	{
		System.out.println(beginDate + " encounterConcepts=null" );
		return listToReturn;
	} else {
		System.out.println(beginDate + " encounterConcepts=" + encounterConcepts.size());
	}
	
				
	System.out.println(beginDate + " procedureConcepts=" + procedureConcepts.size());
	System.out.println(beginDate + " clinicalStatementRelationships=" + clinicalStatementRelationships.size());
	
	for (ProcedureConcept pc : (List<ProcedureConcept>) procedureConcepts)
	{
		String pcHash = pc.getOpenCdsConceptCode() + "|" + pc.getConceptTargetId();
		pcSet.add(pcHash);
	}
	
	if (encounterConcepts != null)
	{
		for (EncounterTypeConcept etc : (List<EncounterTypeConcept>) encounterConcepts) 
		{
			String etcHash = etc.getOpenCdsConceptCode() + "|" + etc.getConceptTargetId();
			etcSet.add(etcHash);
		}
	}
	
	
	for (ProcedureConcept pc : (List<ProcedureConcept>) procedureConcepts)
	{
		String pcConceptCode = pc.getOpenCdsConceptCode();
		String pcId = pc.getConceptTargetId();
		String pcDTMC = pc.getDeterminationMethodCode();
		String pcDN = pc.getDisplayName();
		String pcConceptId = pc.getId();
		
		//find all the CSRs with pcId as targetId
		for (ClinicalStatementRelationship csr : (List<ClinicalStatementRelationship>)clinicalStatementRelationships)
		{
			if (pcId.equals(csr.getTargetId()))
			{
				//found the targetId, so use the sourceId to check for an entry in the etc list
				String sourceId = csr.getSourceId();
				String hashkey = pcConceptCode + "|" + sourceId;
				if ( ! etcSet.contains(hashkey))
				{
					//did not find it, so add the concept to listToReturn
					EncounterTypeConcept newEtc = new EncounterTypeConcept();
					newEtc.setConceptTargetId(pcId);
					newEtc.setOpenCdsConceptCode(pcConceptCode);
					newEtc.setDeterminationMethodCode(pcDTMC);
					newEtc.setDisplayName(pcDN);
					newEtc.setId(pcConceptId + "^generated"); 
					listToReturn.add(newEtc);
					
					//now add it to hashset, so we don't try to create it again...
					etcSet.add(hashkey);
				}
			}
		}
	}
	
	System.out.println(DateUtility.getInstance().getDateAsString(new java.util.Date(), "yyyyMMddHHmmss.SSSZZZZZ") + " newEncTypeConcepts=" + listToReturn.size());
	return listToReturn;		
}

	
	
	
	/**
	 * @param dateList
	 * @param numberOf
	 * @param timeUnits
	 * @return
	 */
	@SuppressWarnings("unchecked")
	int countSeparatedDates(java.util.List dateList, int numberOf, int timeUnits) 
{
/* 
Returns number of dateTimess in dateList with the specified linear separation.  
I.e., counts number of entries that follow a previous entry by the specified separation value ignoring timeUnits smaller than the separation value.

Returns 0 if list is null or empty.

*/

	if (dateList == null) {
		return 0;
	}
	
	System.out.println(">>> countSeparatedDate(dateList): " + dateList.toString());

	// this list contains only dates which have different dates (regardless of hours, minutes and seconds) compared to other dates already included;
	java.util.ArrayList<java.util.Date> separatedDateList = new java.util.ArrayList<java.util.Date>();

	org.opencds.common.utilities.DateUtility dateUtility = org.opencds.common.utilities.DateUtility.getInstance();
	
	// sort the input datelist;
	Collections.sort((java.util.List<java.util.Date>)dateList);
	
	for ( int i = 0; i < dateList.size(); i++ )
	{			
		java.util.Date thisDate = (java.util.Date)dateList.get(i);
		if (i > 0) {
			java.util.Date prevDate = (java.util.Date)dateList.get(i -1);
			java.util.Date prevDateAdjusted = dateUtility.getDateAfterAddingTime(prevDate, numberOf, timeUnits);
			if (prevDateAdjusted.compareTo(thisDate) <= 0) {
				separatedDateList.add(thisDate);
			}
			
		} else {
			separatedDateList.add(thisDate); //count the very first date because it has no dates before it
		}
	}
	System.out.println(">>> countSeparatedDates(separatedDateList): " + separatedDateList.toString());
	return separatedDateList.size();
}	
}
