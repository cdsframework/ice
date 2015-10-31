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

import org.opencds.vmr.v1_0.internal.datatypes.ANY;
import org.opencds.vmr.v1_0.internal.datatypes.CD;
import org.opencds.vmr.v1_0.internal.datatypes.IVLDate;


/**
 * Abstract base class for a goal, which is a clinical end or aim towards which effort is directed.
 */
public abstract class GoalBase
    extends ClinicalStatement
{

    protected CD goalFocus;
    protected CD criticality;
    protected IVLDate goalPursuitEffectiveTime;
    protected IVLDate goalAchievementTargetTime;
    protected BodySite targetBodySite;
    protected GoalValue targetGoalValue;

    /**
     * Gets the value of the goalFocus property.
     * 
     * @return
     *     possible object is
     *     {@link CD }
     *     
     */
    public CD getGoalFocus() {
        return goalFocus;
    }

    /**
     * Sets the value of the goalFocus property.
     * 
     * @param value
     *     allowed object is
     *     {@link CD }
     *     
     */
    public void setGoalFocus(CD value) {
        this.goalFocus = value;
    }

    /**
     * Gets the value of the criticality property.
     * 
     * @return
     *     possible object is
     *     {@link CD }
     *     
     */
    public CD getCriticality() {
        return criticality;
    }

    /**
     * Sets the value of the criticality property.
     * 
     * @param value
     *     allowed object is
     *     {@link CD }
     *     
     */
    public void setCriticality(CD value) {
        this.criticality = value;
    }

    /**
     * Gets the value of the goalPursuitEffectiveTime property.
     * 
     * @return
     *     possible object is
     *     {@link IVLDate }
     *     
     */
    public IVLDate getGoalPursuitEffectiveTime() {
        return goalPursuitEffectiveTime;
    }

    /**
     * Sets the value of the goalPursuitEffectiveTime property.
     * 
     * @param value
     *     allowed object is
     *     {@link IVLDate }
     *     
     */
    public void setGoalPursuitEffectiveTime(IVLDate value) {
        this.goalPursuitEffectiveTime = value;
    }

    /**
     * Gets the value of the goalAchievementTargetTime property.
     * 
     * @return
     *     possible object is
     *     {@link IVLDate }
     *     
     */
    public IVLDate getGoalAchievementTargetTime() {
        return goalAchievementTargetTime;
    }

    /**
     * Sets the value of the goalAchievementTargetTime property.
     * 
     * @param value
     *     allowed object is
     *     {@link IVLDate }
     *     
     */
    public void setGoalAchievementTargetTime(IVLDate value) {
        this.goalAchievementTargetTime = value;
    }

    /**
     * Gets the value of the targetBodySite property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public BodySite getTargetBodySite() {
        return targetBodySite;
    }

    /**
     * Sets the value of the targetBodySite property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTargetBodySite(BodySite value) {
        this.targetBodySite = value;
    }

    /**
     * Gets the value of the targetGoalValue property.
     * 
     * @return
     *     possible object is
     *     {@link ANY }
     *     
     */
    public GoalValue getTargetGoalValue() {
        return targetGoalValue;
    }

    /**
     * Sets the value of the targetGoalValue property.
     * 
     * @param value
     *     allowed object is
     *     {@link ANY }
     *     
     */
    public void setTargetGoalValue(GoalValue value) {
        this.targetGoalValue = value;
    }

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ ((criticality == null) ? 0 : criticality.hashCode());
		result = prime
				* result
				+ ((goalAchievementTargetTime == null) ? 0
						: goalAchievementTargetTime.hashCode());
		result = prime * result
				+ ((goalFocus == null) ? 0 : goalFocus.hashCode());
		result = prime
				* result
				+ ((goalPursuitEffectiveTime == null) ? 0
						: goalPursuitEffectiveTime.hashCode());
		result = prime * result
				+ ((targetBodySite == null) ? 0 : targetBodySite.hashCode());
		result = prime * result
				+ ((targetGoalValue == null) ? 0 : targetGoalValue.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		GoalBase other = (GoalBase) obj;
		if (criticality == null) {
			if (other.criticality != null)
				return false;
		} else if (!criticality.equals(other.criticality))
			return false;
		if (goalAchievementTargetTime == null) {
			if (other.goalAchievementTargetTime != null)
				return false;
		} else if (!goalAchievementTargetTime
				.equals(other.goalAchievementTargetTime))
			return false;
		if (goalFocus == null) {
			if (other.goalFocus != null)
				return false;
		} else if (!goalFocus.equals(other.goalFocus))
			return false;
		if (goalPursuitEffectiveTime == null) {
			if (other.goalPursuitEffectiveTime != null)
				return false;
		} else if (!goalPursuitEffectiveTime
				.equals(other.goalPursuitEffectiveTime))
			return false;
		if (targetBodySite == null) {
			if (other.targetBodySite != null)
				return false;
		} else if (!targetBodySite.equals(other.targetBodySite))
			return false;
		if (targetGoalValue == null) {
			if (other.targetGoalValue != null)
				return false;
		} else if (!targetGoalValue.equals(other.targetGoalValue))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return super.toString() 
				+ ", GoalBase [goalFocus=" + goalFocus + ", criticality="
				+ criticality + ", goalPursuitEffectiveTime="
				+ goalPursuitEffectiveTime + ", goalAchievementTargetTime="
				+ goalAchievementTargetTime + ", targetBodySite="
				+ targetBodySite + ", targetGoalValue=" + targetGoalValue + "]";
	}
    
}
