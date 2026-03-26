package org.opencds.vmr.v1_0.internal;

import java.util.List;

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
public abstract class ProblemBase extends ClinicalStatement
{
    protected CD problemCode;
    protected List<BodySite> affectedBodySite;
    protected IVLDate problemEffectiveTime;
    protected IVLDate diagnosticEventTime;
}
