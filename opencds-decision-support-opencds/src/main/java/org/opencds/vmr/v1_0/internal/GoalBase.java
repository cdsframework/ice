package org.opencds.vmr.v1_0.internal;

import org.opencds.vmr.v1_0.internal.datatypes.CD;
import org.opencds.vmr.v1_0.internal.datatypes.IVLDate;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@EqualsAndHashCode(callSuper = false)
@ToString
public abstract class GoalBase extends ClinicalStatement
{
    private CD goalFocus;
    private CD criticality;
    private IVLDate goalPursuitEffectiveTime;
    private IVLDate goalAchievementTargetTime;
    private BodySite targetBodySite;
    private GoalValue targetGoalValue;
}
