/**
 * Copyright 2011, 2012 OpenCDS.org
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

package org.opencds.vmr.v1_0.mappings.out.structures;

import java.util.List;
import java.util.Map;

import org.opencds.vmr.v1_0.internal.ClinicalStatement;
import org.opencds.vmr.v1_0.internal.EntityBase;
import org.opencds.vmr.v1_0.internal.EntityRelationship;
import org.opencds.vmr.v1_0.schema.EvaluatedPerson.ClinicalStatements;

/**
 * @author David Shields
 * 
 * @date 11-25-2011
 *
 */
public class OrganizedResults {
	protected org.opencds.vmr.v1_0.schema.EvaluatedPerson.ClinicalStatements 	output; 
	protected String								subjectPersonId; 
	protected String								focalPersonId;
	protected Map<String,List<?>>					results;			//key = fact list type, 		value = fact list of type
	protected Map<String,List<ClinicalStatement>> 	csChildren; 		//key = clinical statement id, 	value = clinical statement object
	protected Map<String,EntityBase> 				entityChildren;		//key = entity ID, 				value = entity object
	protected Map<String,List<EntityRelationship>> 	entityRelationships;//key = sourceId, 				value = List of all entity relationship objects for that sourceId
	
	/**
	 * @param output
	 * @param subjectPersonId
	 * @param focalPersonId
	 * @param results
	 * @param csChildren
	 * @param entityInRoleChildren
	 * @param entityList
	 * @param entityChildren
	 */
	public OrganizedResults(
			ClinicalStatements output, 
			String subjectPersonId,
			String focalPersonId, 
			Map<String, List<?>> results,
			Map<String, List<ClinicalStatement>> csChildren,
			Map<String, EntityBase> entityList,
			Map<String, List<EntityRelationship>> entityChildren) {
		this.output = output;
		this.subjectPersonId = subjectPersonId;
		this.focalPersonId = focalPersonId;
		this.results = results;
		this.csChildren = csChildren;
		this.entityChildren = entityList;
		this.entityRelationships = entityChildren;
	}

	/**
	 * @return the output
	 */
	public org.opencds.vmr.v1_0.schema.EvaluatedPerson.ClinicalStatements getOutput() {
		return output;
	}

	/**
	 * @param output the output to set
	 */
	public void setOutput(
			org.opencds.vmr.v1_0.schema.EvaluatedPerson.ClinicalStatements output) {
		this.output = output;
	}

	/**
	 * @return the subjectPersonId
	 */
	public String getSubjectPersonId() {
		return subjectPersonId;
	}

	/**
	 * @param subjectPersonId the subjectPersonId to set
	 */
	public void setSubjectPersonId(String subjectPersonId) {
		this.subjectPersonId = subjectPersonId;
	}

	/**
	 * @return the focalPersonId
	 */
	public String getFocalPersonId() {
		return focalPersonId;
	}

	/**
	 * @param focalPersonId the focalPersonId to set
	 */
	public void setFocalPersonId(String focalPersonId) {
		this.focalPersonId = focalPersonId;
	}

	/**
	 * @return the results
	 */
	public Map<String, List<?>> getResults() {
		return results;
	}

	/**
	 * @param results the results to set
	 */
	public void setResults(Map<String, List<?>> results) {
		this.results = results;
	}

	/**
	 * @return the csChildren
	 */
	public Map<String, List<ClinicalStatement>> getCsChildren() {
		return csChildren;
	}

	/**
	 * @param csChildren the csChildren to set
	 */
	public void setCsChildren(Map<String, List<ClinicalStatement>> csChildren) {
		this.csChildren = csChildren;
	}

	/**
	 * @return the entityList
	 */
	public Map<String, EntityBase> getEntityList() {
		return entityChildren;
	}

	/**
	 * @param entityList the entityList to set
	 */
	public void setEntityList(Map<String, EntityBase> entityList) {
		this.entityChildren = entityList;
	}

	/**
	 * @return the entityChildren
	 */
	public Map<String, List<EntityRelationship>> getEntityChildren() {
		return entityRelationships;
	}

	/**
	 * @param entityChildren the entityChildren to set
	 */
	public void setEntityChildren(Map<String, List<EntityRelationship>> entityChildren) {
		this.entityRelationships = entityChildren;
	}
	
}
