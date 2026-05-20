package org.opencds.vmr.v1_0.internal;

import org.opencds.vmr.v1_0.internal.datatypes.BL;
import org.opencds.vmr.v1_0.internal.datatypes.CD;
import org.opencds.vmr.v1_0.internal.datatypes.PQ;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@EqualsAndHashCode(callSuper = false)
@ToString
public class Problem extends ProblemBase
{
    private CD importance;
    private CD severity;
    private CD problemStatus;
    private PQ ageAtOnset;
    private BL wasCauseOfDeath;
}
