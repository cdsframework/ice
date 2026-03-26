package org.opencds.vmr.v1_0.internal;

import org.opencds.vmr.v1_0.internal.datatypes.CD;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@EqualsAndHashCode(callSuper = false)
@ToString
public abstract class ProcedureBase extends ClinicalStatement
{
    protected CD procedureCode;
    protected CD procedureMethod;
    protected BodySite approachBodySite;
    protected BodySite targetBodySite;
}
