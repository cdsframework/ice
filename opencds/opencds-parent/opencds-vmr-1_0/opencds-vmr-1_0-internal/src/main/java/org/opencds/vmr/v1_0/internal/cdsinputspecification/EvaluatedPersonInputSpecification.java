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
 */

package org.opencds.vmr.v1_0.internal.cdsinputspecification;



/**
 * Specifies the data required for an evaluated person.  Can include (i) a specification of the person attributes (e.g., gender) 
 * required; (ii) a specification of the templates that must be applied; (iii) a specification of the data required for related entities; 
 * and (iv) a specification of the clinical statements required.
 */

public abstract class EvaluatedPersonInputSpecification {

//    protected List<CS> requiredEvaluatedPersonAttribute;
//    protected List<II> requiredEvaluatedPersonTemplate;
//    protected List<RelatedEntityInputSpecification> relatedEntityInputSpecification;
//    protected List<ClinicalStatementInputSpecification> clinicalStatementInputSpecification;

/*    *//**
     * Gets the value of the requiredEvaluatedPersonAttribute property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the requiredEvaluatedPersonAttribute property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getRequiredEvaluatedPersonAttribute().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link CS }
     * 
     * 
     *//*
    public List<CS> getRequiredEvaluatedPersonAttribute() {
        if (requiredEvaluatedPersonAttribute == null) {
            requiredEvaluatedPersonAttribute = new ArrayList<CS>();
        }
        return this.requiredEvaluatedPersonAttribute;
    }

    *//**
     * Gets the value of the requiredEvaluatedPersonTemplate property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the requiredEvaluatedPersonTemplate property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getRequiredEvaluatedPersonTemplate().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link II }
     * 
     * 
     *//*
    public List<II> getRequiredEvaluatedPersonTemplate() {
        if (requiredEvaluatedPersonTemplate == null) {
            requiredEvaluatedPersonTemplate = new ArrayList<II>();
        }
        return this.requiredEvaluatedPersonTemplate;
    }

    *//**
     * Gets the value of the relatedEntityInputSpecification property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the relatedEntityInputSpecification property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getRelatedEntityInputSpecification().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link RelatedEntityInputSpecification }
     * 
     * 
     *//*
    public List<RelatedEntityInputSpecification> getRelatedEntityInputSpecification() {
        if (relatedEntityInputSpecification == null) {
            relatedEntityInputSpecification = new ArrayList<RelatedEntityInputSpecification>();
        }
        return this.relatedEntityInputSpecification;
    }

    *//**
     * Gets the value of the clinicalStatementInputSpecification property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the clinicalStatementInputSpecification property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getClinicalStatementInputSpecification().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ClinicalStatementInputSpecification }
     * 
     * 
     *//*
    public List<ClinicalStatementInputSpecification> getClinicalStatementInputSpecification() {
        if (clinicalStatementInputSpecification == null) {
            clinicalStatementInputSpecification = new ArrayList<ClinicalStatementInputSpecification>();
        }
        return this.clinicalStatementInputSpecification;
    }

	public void setRequiredEvaluatedPersonAttribute(
			List<CS> requiredEvaluatedPersonAttribute) {
		this.requiredEvaluatedPersonAttribute = requiredEvaluatedPersonAttribute;
	}

	public void setRequiredEvaluatedPersonTemplate(
			List<II> requiredEvaluatedPersonTemplate) {
		this.requiredEvaluatedPersonTemplate = requiredEvaluatedPersonTemplate;
	}

	public void setRelatedEntityInputSpecification(
			List<RelatedEntityInputSpecification> relatedEntityInputSpecification) {
		this.relatedEntityInputSpecification = relatedEntityInputSpecification;
	}

	public void setClinicalStatementInputSpecification(
			List<ClinicalStatementInputSpecification> clinicalStatementInputSpecification) {
		this.clinicalStatementInputSpecification = clinicalStatementInputSpecification;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime
				* result
				+ ((clinicalStatementInputSpecification == null) ? 0
						: clinicalStatementInputSpecification.hashCode());
		result = prime
				* result
				+ ((relatedEntityInputSpecification == null) ? 0
						: relatedEntityInputSpecification.hashCode());
		result = prime
				* result
				+ ((requiredEvaluatedPersonAttribute == null) ? 0
						: requiredEvaluatedPersonAttribute.hashCode());
		result = prime
				* result
				+ ((requiredEvaluatedPersonTemplate == null) ? 0
						: requiredEvaluatedPersonTemplate.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		EvaluatedPersonInputSpecification other = (EvaluatedPersonInputSpecification) obj;
		if (clinicalStatementInputSpecification == null) {
			if (other.clinicalStatementInputSpecification != null)
				return false;
		} else if (!clinicalStatementInputSpecification
				.equals(other.clinicalStatementInputSpecification))
			return false;
		if (relatedEntityInputSpecification == null) {
			if (other.relatedEntityInputSpecification != null)
				return false;
		} else if (!relatedEntityInputSpecification
				.equals(other.relatedEntityInputSpecification))
			return false;
		if (requiredEvaluatedPersonAttribute == null) {
			if (other.requiredEvaluatedPersonAttribute != null)
				return false;
		} else if (!requiredEvaluatedPersonAttribute
				.equals(other.requiredEvaluatedPersonAttribute))
			return false;
		if (requiredEvaluatedPersonTemplate == null) {
			if (other.requiredEvaluatedPersonTemplate != null)
				return false;
		} else if (!requiredEvaluatedPersonTemplate
				.equals(other.requiredEvaluatedPersonTemplate))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "EvaluatedPersonInputSpecification [requiredEvaluatedPersonAttribute="
				+ requiredEvaluatedPersonAttribute
				+ ", requiredEvaluatedPersonTemplate="
				+ requiredEvaluatedPersonTemplate
				+ ", relatedEntityInputSpecification="
				+ relatedEntityInputSpecification
				+ ", clinicalStatementInputSpecification="
				+ clinicalStatementInputSpecification + "]";
	}
*/
}
