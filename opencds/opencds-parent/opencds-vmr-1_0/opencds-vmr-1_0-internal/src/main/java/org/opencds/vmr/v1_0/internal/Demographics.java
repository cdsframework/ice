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

import java.util.List;

import org.opencds.vmr.v1_0.internal.datatypes.AD;
import org.opencds.vmr.v1_0.internal.datatypes.BL;
import org.opencds.vmr.v1_0.internal.datatypes.CD;
import org.opencds.vmr.v1_0.internal.datatypes.EN;
import org.opencds.vmr.v1_0.internal.datatypes.PQ;
import org.opencds.vmr.v1_0.internal.datatypes.TEL;


/**
 * An evaluated person who is the subject of evaluation by a CDS system may be the focal patient or some other 
 * relevant person (e.g., a relative or a sexual contact). 
 * 
 * This class defines demographic attributes of evaluated persons.  Clinical statements and related entities
 * belonging to an EvaluatedPerson are defined in other classes.
 * 			
 */

public class Demographics {

//    public static class Demographics {

    protected java.util.Date birthTime;
    protected PQ age;
    protected CD gender;
    protected List<CD> race;
    protected List<CD> ethnicity;
    protected List<EN> name;
    protected List<AD> address;
    protected List<TEL> telecom;
    protected BL isDeceased;
    protected PQ ageAtDeath;
    protected CD preferredLanguage;
	/**
	 * @return the birthTime
	 */
	public java.util.Date getBirthTime() {
		return birthTime;
	}
	/**
	 * @param birthTime the birthTime to set
	 */
	public void setBirthTime(java.util.Date birthTime) {
		this.birthTime = birthTime;
	}
	/**
	 * @return the age
	 */
	public PQ getAge() {
		return age;
	}
	/**
	 * @param age the age to set
	 */
	public void setAge(PQ age) {
		this.age = age;
	}
	/**
	 * @return the gender
	 */
	public CD getGender() {
		return gender;
	}
	/**
	 * @param gender the gender to set
	 */
	public void setGender(CD gender) {
		this.gender = gender;
	}
	/**
	 * @return the race
	 */
	public List<CD> getRace() {
		return race;
	}
	/**
	 * @param race the race to set
	 */
	public void setRace(List<CD> race) {
		this.race = race;
	}
	/**
	 * @return the ethnicity
	 */
	public List<CD> getEthnicity() {
		return ethnicity;
	}
	/**
	 * @param ethnicity the ethnicity to set
	 */
	public void setEthnicity(List<CD> ethnicity) {
		this.ethnicity = ethnicity;
	}
	/**
	 * @return the name
	 */
	public List<EN> getName() {
		return name;
	}
	/**
	 * @param name the name to set
	 */
	public void setName(List<EN> name) {
		this.name = name;
	}
	/**
	 * @return the address
	 */
	public List<AD> getAddress() {
		return address;
	}
	/**
	 * @param address the address to set
	 */
	public void setAddress(List<AD> address) {
		this.address = address;
	}
	/**
	 * @return the telecom
	 */
	public List<TEL> getTelecom() {
		return telecom;
	}
	/**
	 * @param telecom the telecom to set
	 */
	public void setTelecom(List<TEL> telecom) {
		this.telecom = telecom;
	}
	/**
	 * @return the isDeceased
	 */
	public BL getIsDeceased() {
		return isDeceased;
	}
	/**
	 * @param isDeceased the isDeceased to set
	 */
	public void setIsDeceased(BL isDeceased) {
		this.isDeceased = isDeceased;
	}
	/**
	 * @return the ageAtDeath
	 */
	public PQ getAgeAtDeath() {
		return ageAtDeath;
	}
	/**
	 * @param ageAtDeath the ageAtDeath to set
	 */
	public void setAgeAtDeath(PQ ageAtDeath) {
		this.ageAtDeath = ageAtDeath;
	}
	/**
	 * @return the preferredLanguage
	 */
	public CD getPreferredLanguage() {
		return preferredLanguage;
	}
	/**
	 * @param preferredLanguage the preferredLanguage to set
	 */
	public void setPreferredLanguage(CD preferredLanguage) {
		this.preferredLanguage = preferredLanguage;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((address == null) ? 0 : address.hashCode());
		result = prime * result + ((age == null) ? 0 : age.hashCode());
		result = prime * result
				+ ((ageAtDeath == null) ? 0 : ageAtDeath.hashCode());
		result = prime * result
				+ ((birthTime == null) ? 0 : birthTime.hashCode());
		result = prime * result
				+ ((ethnicity == null) ? 0 : ethnicity.hashCode());
		result = prime * result
				+ ((gender == null) ? 0 : gender.hashCode());
		result = prime * result
				+ ((isDeceased == null) ? 0 : isDeceased.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime
				* result
				+ ((preferredLanguage == null) ? 0 : preferredLanguage
						.hashCode());
		result = prime * result + ((race == null) ? 0 : race.hashCode());
		result = prime * result
				+ ((telecom == null) ? 0 : telecom.hashCode());
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
		Demographics other = (Demographics) obj;
		if (address == null) {
			if (other.address != null)
				return false;
		} else if (!address.equals(other.address))
			return false;
		if (age == null) {
			if (other.age != null)
				return false;
		} else if (!age.equals(other.age))
			return false;
		if (ageAtDeath == null) {
			if (other.ageAtDeath != null)
				return false;
		} else if (!ageAtDeath.equals(other.ageAtDeath))
			return false;
		if (birthTime == null) {
			if (other.birthTime != null)
				return false;
		} else if (!birthTime.equals(other.birthTime))
			return false;
		if (ethnicity == null) {
			if (other.ethnicity != null)
				return false;
		} else if (!ethnicity.equals(other.ethnicity))
			return false;
		if (gender == null) {
			if (other.gender != null)
				return false;
		} else if (!gender.equals(other.gender))
			return false;
		if (isDeceased == null) {
			if (other.isDeceased != null)
				return false;
		} else if (!isDeceased.equals(other.isDeceased))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (preferredLanguage == null) {
			if (other.preferredLanguage != null)
				return false;
		} else if (!preferredLanguage.equals(other.preferredLanguage))
			return false;
		if (race == null) {
			if (other.race != null)
				return false;
		} else if (!race.equals(other.race))
			return false;
		if (telecom == null) {
			if (other.telecom != null)
				return false;
		} else if (!telecom.equals(other.telecom))
			return false;
		return true;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Demographics [birthTime=" + birthTime + ", age=" + age
				+ ", gender=" + gender + ", race=" + race + ", ethnicity="
				+ ethnicity + ", name=" + name + ", address=" + address
				+ ", telecom=" + telecom + ", isDeceased=" + isDeceased
				+ ", ageAtDeath=" + ageAtDeath + ", preferredLanguage="
				+ preferredLanguage + ", toString()=" + super.toString()
				+ "]";
	}

}
