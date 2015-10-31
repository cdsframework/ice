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

/**
 * <p>The Person's age at the EvalTime.  Note that multiple ages are created using
 * the DOB.  In the future, intend to extend mappings so that various age values, and 
 * potentially approximate DOB, can be derived from Observation of a
 * person's approximate age.   
 * 
 */
public class EvaluatedPersonAgeAtEvalTime    
{		
	protected String personId;
	protected Integer age; // e.g., "5" if person is 5.3 years old
	protected String ageUnit; // use values defined in this class 
	
//	public static String AGE_UNIT_YEAR = "year";
//	public static String AGE_UNIT_MONTH = "month";
//	public static String AGE_UNIT_WEEK = "week";
//	public static String AGE_UNIT_DAY = "day";
//	public static String AGE_UNIT_HOUR = "hour";
//	public static String AGE_UNIT_MINUTE = "minute";
//	public static String AGE_UNIT_SECOND = "second";

/*
 * above code changed to below for consistency with the codes in OpenCDS Apelon DTS
 * and the codes therefore used in the Guvnor enumerations for ageUnits.
 * 	-- des 2012-09-24
 */
	
	public static String AGE_UNIT_YEAR = "1";
	public static String AGE_UNIT_MONTH = "2";
	public static String AGE_UNIT_WEEK = "3";
	public static String AGE_UNIT_DAY = "5";
	public static String AGE_UNIT_HOUR = "11";
	public static String AGE_UNIT_MINUTE = "12";
	public static String AGE_UNIT_SECOND = "13";
	
	
    /** 
     * generated getters and setters, hash code and equals(), and toString() methods follow 
     */
	public String getPersonId() {
		return personId;
	}
	public void setPersonId(String personId) {
		this.personId = personId;
	}
	public Integer getAge() {
		return age;
	}
	public void setAge(Integer age) {
		this.age = age;
	}
	public String getAgeUnit() {
		return ageUnit;
	}
	public void setAgeUnit(String ageUnit) {
		this.ageUnit = ageUnit;
	}
	public static String getAGE_UNIT_YEAR() {
		return AGE_UNIT_YEAR;
	}
	public static void setAGE_UNIT_YEAR(String aGE_UNIT_YEAR) {
		AGE_UNIT_YEAR = aGE_UNIT_YEAR;
	}
	public static String getAGE_UNIT_MONTH() {
		return AGE_UNIT_MONTH;
	}
	public static void setAGE_UNIT_MONTH(String aGE_UNIT_MONTH) {
		AGE_UNIT_MONTH = aGE_UNIT_MONTH;
	}
	public static String getAGE_UNIT_WEEK() {
		return AGE_UNIT_WEEK;
	}
	public static void setAGE_UNIT_WEEK(String aGE_UNIT_WEEK) {
		AGE_UNIT_WEEK = aGE_UNIT_WEEK;
	}
	public static String getAGE_UNIT_DAY() {
		return AGE_UNIT_DAY;
	}
	public static void setAGE_UNIT_DAY(String aGE_UNIT_DAY) {
		AGE_UNIT_DAY = aGE_UNIT_DAY;
	}
	public static String getAGE_UNIT_HOUR() {
		return AGE_UNIT_HOUR;
	}
	public static void setAGE_UNIT_HOUR(String aGE_UNIT_HOUR) {
		AGE_UNIT_HOUR = aGE_UNIT_HOUR;
	}
	public static String getAGE_UNIT_MINUTE() {
		return AGE_UNIT_MINUTE;
	}
	public static void setAGE_UNIT_MINUTE(String aGE_UNIT_MINUTE) {
		AGE_UNIT_MINUTE = aGE_UNIT_MINUTE;
	}
	public static String getAGE_UNIT_SECOND() {
		return AGE_UNIT_SECOND;
	}
	public static void setAGE_UNIT_SECOND(String aGE_UNIT_SECOND) {
		AGE_UNIT_SECOND = aGE_UNIT_SECOND;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((age == null) ? 0 : age.hashCode());
		result = prime * result + ((ageUnit == null) ? 0 : ageUnit.hashCode());
		result = prime * result
				+ ((personId == null) ? 0 : personId.hashCode());
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
		EvaluatedPersonAgeAtEvalTime other = (EvaluatedPersonAgeAtEvalTime) obj;
		if (age == null) {
			if (other.age != null)
				return false;
		} else if (!age.equals(other.age))
			return false;
		if (ageUnit == null) {
			if (other.ageUnit != null)
				return false;
		} else if (!ageUnit.equals(other.ageUnit))
			return false;
		if (personId == null) {
			if (other.personId != null)
				return false;
		} else if (!personId.equals(other.personId))
			return false;
		return true;
	}
	@Override
	public String toString() {
		return "EvaluatedPersonAgeAtEvalTime [personId=" + personId + ", age=" + age
				+ ", ageUnit=" + ageUnit + "]";
	}
}
