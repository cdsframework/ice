/*
 * Copyright (C) 2015 New York City Department of Health and Mental Hygiene, Bureau of Immunization
 * Contributions by HLN Consulting, LLC
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU
 * Lesser General Public License as published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version. You should have received a copy of the GNU Lesser
 * General Public License along with this program. If not, see <http://www.gnu.org/licenses/> for more
 * details.
 *
 * The above-named contributors (HLN Consulting, LLC) are also licensed by the New York City
 * Department of Health and Mental Hygiene, Bureau of Immunization to have (without restriction,
 * limitation, and warranty) complete irrevocable access and rights to this project.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; THE
 *
 * SOFTWARE IS PROVIDED "AS IS" WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING,
 * BUT NOT LIMITED TO, WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE COPYRIGHT HOLDERS, IF ANY, OR DEVELOPERS BE LIABLE FOR
 * ANY CLAIM, DAMAGES, OR OTHER LIABILITY OF ANY KIND, ARISING FROM, OUT OF, OR IN CONNECTION WITH
 * THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 * For more information about this software, see http://www.hln.com/ice or send
 * correspondence to ice@hln.com.
 */
 
package org.cdsframework.ice.supportingdata;

import java.util.Collection;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cdsframework.ice.service.ICEConcept;
import org.opencds.common.exceptions.ImproperUsageException;

public class LocallyCodedVaccineGroupItem {
	
	/**
	 * 
		<ns2:iceVaccineGroupSpecificationFile xmlns:ns2="org.cdsframework.util.support.data.ice.vaccinegroup">
	    	<vaccineGroup code="100" codeSystem="2.16.840.1.113883.3.795.12.100.1" codeSystemName="ICE Vaccine Group" displayName="HepB"/>
	    	<cdsVersion>org.nyc.cir^ICE^1.0.0</cdsVersion>
	    	<ns2:priority>10</ns2:priority>
	    	<relatedVaccine code="104" codeSystem="2.16.840.1.113883.12.292" codeSystemName="CVX" displayName="Hep A-Hep B"/>
		    <relatedVaccine code="43" codeSystem="2.16.840.1.113883.12.292" codeSystemName="CVX" displayName="Hep B, adult"/>
		    <relatedVaccine code="42" codeSystem="2.16.840.1.113883.12.292" codeSystemName="CVX" displayName="Hep B, adolescent/high risk infant"/>
		    <relatedVaccine code="44" codeSystem="2.16.840.1.113883.12.292" codeSystemName="CVX" displayName="Hep B, dialysis"/>
		    <relatedVaccine code="102" codeSystem="2.16.840.1.113883.12.292" codeSystemName="CVX" displayName="DTP-Hib-Hep B"/>
		    <relatedVaccine code="45" codeSystem="2.16.840.1.113883.12.292" codeSystemName="CVX" displayName="Hep B NOS"/>
		    <relatedVaccine code="110" codeSystem="2.16.840.1.113883.12.292" codeSystemName="CVX" displayName="DTaP-Hep B-IPV"/>
		    <relatedVaccine code="08" codeSystem="2.16.840.1.113883.12.292" codeSystemName="CVX" displayName="Hep B, adolescent or pediatric"/>
		    <relatedVaccine code="51" codeSystem="2.16.840.1.113883.12.292" codeSystemName="CVX" displayName="Hib-Hep B"/>
		    <diseaseImmunity code="070.30" codeSystem="2.16.840.1.113883.6.103" codeSystemName="ICE Disease" displayName="Hepatitis B"/>
		    <openCdsMembership code="ICE202" displayName="Immunization Evaluation (Hep B Vaccine Group)"/>
		    <primaryOpenCdsConcept code="ICE202" displayName="Immunization Evaluation (Hep B Vaccine Group)"/>
		</ns2:iceVaccineGroupSpecificationFile>
	 */
	private String vaccineGroupCdsListItemName;
	private Collection<String> relatedDiseasesCdsListItemNames;
	private int priority;
	private ICEConcept primaryOpenCdsConcept;
	private Collection<String> cdsVersions;
	
	private static Log logger = LogFactory.getLog(LocallyCodedVaccineGroupItem.class);	


	// public LocallyCodedVaccineGroupItem(CD pVaccineGroupCD, CD pPrimaryOpenCdsMembership, List<String> pCdsVersions) 
	protected LocallyCodedVaccineGroupItem(String pVaccineGroupCdsListItemName, Collection<String> pCdsVersions, Collection<String> pRelatedDiseasesCdsListItemNames, ICEConcept pPrimaryOpenCdsMembership) 
		throws ImproperUsageException {

		String _METHODNAME = "VaccineGroupItem(): ";
		if (pVaccineGroupCdsListItemName == null || pCdsVersions == null || pVaccineGroupCdsListItemName.isEmpty() || pCdsVersions.isEmpty()) {
			String lErrStr = "Vaccine Group List Item Name and/or CdsVersions not specified";
			logger.error(_METHODNAME + lErrStr);
			throw new ImproperUsageException(lErrStr);
		}
		
		// Check to make sure that the opencds concept code and code system are specified in the CD 
		if (pPrimaryOpenCdsMembership == null || pPrimaryOpenCdsMembership.getOpenCdsConceptCode() == null) {
			String lErrStr = "OpenCDS primary concept membership not specified or properly specified";
			logger.error(_METHODNAME + lErrStr);
			throw new ImproperUsageException(lErrStr);
		}
		
		// Check to make sure related vaccines list is specified
		if (pRelatedDiseasesCdsListItemNames == null) {
			String lErrStr = "Related vaccines not specified";
			logger.error(_METHODNAME + lErrStr);
			throw new ImproperUsageException(lErrStr);
		}
		
		this.vaccineGroupCdsListItemName = pVaccineGroupCdsListItemName;
		this.cdsVersions = pCdsVersions;
		this.primaryOpenCdsConcept = pPrimaryOpenCdsMembership;
		this.relatedDiseasesCdsListItemNames = pRelatedDiseasesCdsListItemNames;
		this.priority = 0;
	}
	
	
	// protected LocallyCodedVaccineGroupItem(CD pVaccineGroupCD, CD pPrimaryOpenCdsMembership, List<String> pCdsVersions, int pPriority) 
	protected LocallyCodedVaccineGroupItem(String pVaccineGroupCdsListItemName, Collection<String> pCdsVersions, Collection<String> pRelatedDiseasesCdsListItemNames, ICEConcept pPrimaryOpenCdsMembership, int pPriority) 
		throws ImproperUsageException {
		
		this(pVaccineGroupCdsListItemName, pCdsVersions, pRelatedDiseasesCdsListItemNames, pPrimaryOpenCdsMembership);
		this.priority = pPriority;
	}

	
	public String getVaccineGroupItem() {
		
		return this.vaccineGroupCdsListItemName;
	}


	public Collection<String> getRelatedDiseasesCdsListItemNames() {
		return relatedDiseasesCdsListItemNames;
	}


	public Collection<String> getCdsVersions() {
		return cdsVersions;
	}


	public int getPriority() {
		return priority;
	}


	public ICEConcept getPrimaryOpenCdsConcept() {
		return primaryOpenCdsConcept;
	}

	@Override
	public String toString() {
		String lStr = "LocallyCodedVaccineGroupItem [vaccineGroupCdsListItemName=" + vaccineGroupCdsListItemName + ", priority=" + priority + ", primaryOpenCdsConcept="	+ primaryOpenCdsConcept; 
		lStr += "\ncdsListVersions= [";

		for (String lDisease : getRelatedDiseasesCdsListItemNames()) {
			lStr += "\tRelatedDiseaseCdsListItemName=" + lDisease + "\n";
		}
		lStr += "\t]\n";		
		lStr += "]";

		for (String lVersionStr : getCdsVersions()) {
			lStr += "\tCdsVersion=" + lVersionStr + "\n";
		}
		lStr += "\t]\n";		
		lStr += "]";
		
		return lStr;
	}
	
}
