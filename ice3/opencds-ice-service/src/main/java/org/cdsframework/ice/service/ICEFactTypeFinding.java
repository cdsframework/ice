package org.cdsframework.ice.service;

import org.cdsframework.ice.service.TargetSeries;

public class ICEFactTypeFinding {

	private String iceResultFinding;
	private TargetDose associatedTargetDose;
	private TargetSeries associatedTargetSeries;
	
	public ICEFactTypeFinding(String pIceResultFinding) {
		this.iceResultFinding = pIceResultFinding;
	}
	
	public ICEFactTypeFinding(String pIceResultFinding, TargetDose pTargetDose) {
		this.iceResultFinding = pIceResultFinding;
		this.associatedTargetDose = pTargetDose;
		this.associatedTargetSeries = pTargetDose != null ? pTargetDose.getAssociatedTargetSeries() : null;
	}

	public ICEFactTypeFinding(String pIceResultFinding, TargetSeries pTargetSeries) {
		this.iceResultFinding = pIceResultFinding;
		this.associatedTargetSeries = pTargetSeries;
	}

	public String getIceResultFinding() {
		return iceResultFinding;
	}
	
	public TargetDose getAssociatedTargetDose() {
		return associatedTargetDose;
	}
	
	/**
	 * Invokes getAssociatedTargetDose()
	 * @return TargetDose associated with the IceFactTypeFinding, if any
	 */
	public TargetDose getTargetDose() {
		return associatedTargetDose;
	}
	
	public TargetSeries getAssociatedTargetSeries() {
		return associatedTargetSeries;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime
				* result
				+ ((iceResultFinding == null) ? 0 : iceResultFinding.hashCode());
		result = prime * result
				+ ((associatedTargetDose == null) ? 0 : associatedTargetDose.hashCode());
		result = prime * result
				+ ((associatedTargetSeries == null) ? 0 : associatedTargetSeries.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof ICEFactTypeFinding))
			return false;
		ICEFactTypeFinding other = (ICEFactTypeFinding) obj;
		if (iceResultFinding == null) {
			if (other.iceResultFinding != null)
				return false;
		} else if (!iceResultFinding.equals(other.iceResultFinding))
			return false;
		if (associatedTargetDose == null) {
			if (other.associatedTargetDose != null)
				return false;
		} else if (!associatedTargetDose.equals(other.associatedTargetDose))
			return false;
		if (associatedTargetSeries == null) {
			if (other.associatedTargetSeries != null)
				return false;
		} else if (!associatedTargetSeries.equals(other.associatedTargetSeries))
			return false;
		return true;
	}
}

