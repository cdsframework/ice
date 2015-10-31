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
import org.opencds.vmr.v1_0.internal.datatypes.EN;
import org.opencds.vmr.v1_0.internal.datatypes.TEL;


/**
 * A human being.
 */

public class Person extends EntityBase {

	 protected List<EN> name;
	 protected List<AD> address;
	 protected List<TEL> telecom;

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

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((address == null) ? 0 : address.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((telecom == null) ? 0 : telecom.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		Person other = (Person) obj;
		if (address == null) {
			if (other.address != null)
				return false;
		} else if (!address.equals(other.address))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
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
		return "Person [" + super.toString() + ", name=" + name + ", address=" + address + ", telecom="
				+ telecom + "]";
	}

//	/* (non-Javadoc)
//	 * @see java.lang.Object#toString()
//	 */
//	@Override
//	public String toString() {
//		//		String name, address, telecom;
//		//		name = someFunctionCallToGetTheName(super.id);
//		//		address = someFunctionCallToGetTheAddress(super.id);
//		//		telecom = someFunctionCallToGetTheTelecom(super.id);
//		return "Person [toString()=" + super.toString() + "]";
//		//			+ ", name=" + name
//		//			+ ", address=" + address
//		//			+ ", telecom=" + telecom	
//	}

/*
	public void main(String[] args ) {
		Person joe = new Person();
		List<EN> name = new ArrayList<EN>();
		EN en = new EN();
		List<ENXP> joesNamePartList = new ArrayList<ENXP>();
		ENXP joesFN = new ENXP();
		joesFN.setValue("Joe");
		joesFN.setType(EntityNamePartType.GIV);
		joesNamePartList.add(joesFN);
		ENXP joesLN = new ENXP();
		joesLN.setValue("Smith");
		joesLN.setType(EntityNamePartType.FAM);
		joesNamePartList.add(joesLN);
		en.setPart(joesNamePartList);
		name.add(en);
		joe.setName(name);
		System.out.println(joe);
	}
*/
}
