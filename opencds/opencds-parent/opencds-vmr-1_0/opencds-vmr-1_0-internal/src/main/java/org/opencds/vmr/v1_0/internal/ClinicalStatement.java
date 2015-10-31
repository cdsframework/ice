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

package org.opencds.vmr.v1_0.internal;

import java.util.Arrays;
import java.util.List;

import org.opencds.vmr.v1_0.internal.datatypes.CD;

/**
 * A record of something of clinical relevance that is being done, has been done, can be done, or is intended or requested to be done.  An abstract base class that serves as the basis for concrete clinical statements, such as ObservationEvent and ProcedureProposal.
 * 
 * Naming and modeling conventions:
 * 
 * 	- in general, attribute names end in 'Code' if and only if the name of the attribute overlaps with the name of the parent class
 * 
 * 
 * 	- times are named as follows: 
 * 		Time is the default suffix for these attributes.  
 * 		EventTime is used to distinguish the time an order is placed vs. when the ordered act should take place. 
 * 		EffectiveTime and TimeInterval are used when there is a desire to emphasize that a prolonged time interval 
 * 			(e.g., > 1 day) can be used rather than a point in time or a short time interval.  
 * 		Note that regardless of the naming convention, IVL_TS attributes allow time intervals of any length.
 * 
 * 
 * 	- subjectEffectiveTime is the time that is primarily related to the subject's experience of disease or 
 * 		treatment events (or durations), rather than when those events were reported or recorded by the performer
 * 
 * 
 * 	- performerEventTime is the event time that is primarily related to the performer, rather than the subject.
 * 
 * 
 * 	- the state between ordering and the ordered event occurring is modeled only in cases of procedures and encounters, 
 * 		due to the substantial rate at which orders do not result in events.
 * 
 * 
 * Approaches to representing specific statements:
 * 
 * 	- No known allergies --> DeniedAdverseEvent with adverseEventAgentCode that is the generic root-level code 
 * 		for substances and adverseEventCode that its the generic root-level code for adverse events.
 * 
 * 
 * 	- No known drug allergies --> DeniedAdverseEvent with adverseEventAgentCode that is the root-level code 
 * 		for medications and adverseEventCode that its the generic root-level code for adverse events.
 * 
 * 
 * 	- No known food allergies --> DeniedAdverseEvent with adverseEventAgentCode that is the root-level code 
 * 		for food and adverseEventCode that its the generic root-level code for adverse events.
 * 
 * 
 * 	- No known medications --> UndeliveredSubstanceAdministration with substance that is the root-level code for medications.
 * 
 * 
 * 	- No known problems --> DeniedProblem with problemCode that is the root-level code for problems or conditions.
 * 
 * 
 * 	- Patient takes an unknown drug --> SubstanceAdministrationEvent where code for substance represents "unknown medication".
 * 
 * <p>Java class for ClinicalStatement complex type.
 * 
 * 
 */
public abstract class ClinicalStatement {

    protected String[] templateId;
	protected String id;
    protected CD dataSourceType;
	protected String evaluatedPersonId;			// Note: this value is populated from the external VMR through context conduction
	protected boolean subjectIsFocalPerson;		// Note: this value is populated from the external VMR through context conduction
												//		when the evaluatedPersonId represents focal person (root) of entire VMR
	protected boolean clinicalStatementToBeRoot;// Note: this value is set to true by the input mapper if this clinical statement
												//		was found as a root entry in the list of clinical statements
												//		It can be modified by rules, if required, and must be properly set for
												//		new CS created by rules, or the new CS may not appear in output.  This
												//		setting will be ignored if a CS is the target of a CS Relationship.
	protected boolean toBeReturned;				// Note: this value is set true when the rules want a particular Clinical Statement 
												// 		to be part of the CDSOutput (ie, the output of the evaluation).  It only 
												//		needs to be set on Root CS, because all nested CS within the Root are returned
												//		regardless of this setting.
	protected List<RelationshipToSource> relationshipToSources;	//Note: list of ClinicalStatement sourceIds and
												//		targetRelationshipToSource values that this CS is related to.  It is
												//		automatically populated during initial output mapping, and referenced to
												//		build any nested output structures.  This list should NOT be modified
												//		by rules.
	
	/**
	 * @return the templateId
	 */
	public String[] getTemplateId() {
		return templateId;
	}

	/**
	 * @param templateId the templateId to set
	 */
	public void setTemplateId(String[] templateId) {
		this.templateId = templateId;
	}

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @return the dataSourceType
	 */
	public CD getDataSourceType() {
		return dataSourceType;
	}

	/**
	 * @param dataSourceType the dataSourceType to set
	 */
	public void setDataSourceType(CD dataSourceType) {
		this.dataSourceType = dataSourceType;
	}

	/**
	 * @return the evaluatedPersonId
	 */
	public String getEvaluatedPersonId() {
		return evaluatedPersonId;
	}

	/**
	 * @param evaluatedPersonId the evaluatedPersonId to set
	 */
	public void setEvaluatedPersonId(String evaluatedPersonId) {
		this.evaluatedPersonId = evaluatedPersonId;
	}

	/**
	 * @return the subjectIsFocalPerson
	 */
	public boolean isSubjectIsFocalPerson() {
		return subjectIsFocalPerson;
	}

	/**
	 * @param subjectIsFocalPerson the subjectIsFocalPerson to set
	 */
	public void setSubjectIsFocalPerson(boolean subjectIsFocalPerson) {
		this.subjectIsFocalPerson = subjectIsFocalPerson;
	}

	/**
	 * @return the clinicalStatementToBeRoot
	 */
	public boolean isClinicalStatementToBeRoot() {
		return clinicalStatementToBeRoot;
	}

	/**
	 * @param clinicalStatementToBeRoot the clinicalStatementToBeRoot to set
	 */
	public void setClinicalStatementToBeRoot(boolean clinicalStatementToBeRoot) {
		this.clinicalStatementToBeRoot = clinicalStatementToBeRoot;
	}

	/**
	 * @return the toBeReturned
	 */
	public boolean isToBeReturned() {
		return toBeReturned;
	}

	/**
	 * @param toBeReturned the toBeReturned to set
	 */
	public void setToBeReturned(boolean toBeReturned) {
		this.toBeReturned = toBeReturned;
	}

	/**
	 * @return the relationshipToSources
	 */
	public List<RelationshipToSource> getRelationshipToSources() {
		return relationshipToSources;
	}

	/**
	 * @param relationshipToSources the relationshipToSources to set
	 */
	public void setRelationshipToSources(
			List<RelationshipToSource> relationshipToSources) {
		this.relationshipToSources = relationshipToSources;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (clinicalStatementToBeRoot ? 1231 : 1237);
		result = prime * result
				+ ((dataSourceType == null) ? 0 : dataSourceType.hashCode());
		result = prime
				* result
				+ ((evaluatedPersonId == null) ? 0 : evaluatedPersonId
						.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime
				* result
				+ ((relationshipToSources == null) ? 0 : relationshipToSources
						.hashCode());
		result = prime * result + (subjectIsFocalPerson ? 1231 : 1237);
		result = prime * result + Arrays.hashCode(templateId);
		result = prime * result + (toBeReturned ? 1231 : 1237);
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ClinicalStatement other = (ClinicalStatement) obj;
		if (clinicalStatementToBeRoot != other.clinicalStatementToBeRoot)
			return false;
		if (dataSourceType == null) {
			if (other.dataSourceType != null)
				return false;
		} else if (!dataSourceType.equals(other.dataSourceType))
			return false;
		if (evaluatedPersonId == null) {
			if (other.evaluatedPersonId != null)
				return false;
		} else if (!evaluatedPersonId.equals(other.evaluatedPersonId))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (relationshipToSources == null) {
			if (other.relationshipToSources != null)
				return false;
		} else if (!relationshipToSources.equals(other.relationshipToSources))
			return false;
		if (subjectIsFocalPerson != other.subjectIsFocalPerson)
			return false;
		if (!Arrays.equals(templateId, other.templateId))
			return false;
		if (toBeReturned != other.toBeReturned)
			return false;
		return true;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "ClinicalStatement [templateId=" + Arrays.toString(templateId)
				+ ", id=" + id + ", dataSourceType=" + dataSourceType
				+ ", evaluatedPersonId=" + evaluatedPersonId
				+ ", subjectIsFocalPerson=" + subjectIsFocalPerson
				+ ", clinicalStatementToBeRoot=" + clinicalStatementToBeRoot
				+ ", toBeReturned=" + toBeReturned + ", relationshipToSources="
				+ relationshipToSources + "]";
	}

}
