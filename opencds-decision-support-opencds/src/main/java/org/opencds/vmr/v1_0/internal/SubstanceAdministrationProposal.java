package org.opencds.vmr.v1_0.internal;

import org.opencds.vmr.v1_0.internal.datatypes.CD;
import org.opencds.vmr.v1_0.internal.datatypes.INT;
import org.opencds.vmr.v1_0.internal.datatypes.IVLDate;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@EqualsAndHashCode(callSuper = false)
@Getter
@Setter
@ToString
public class SubstanceAdministrationProposal extends SubstanceAdministrationBase
{
    protected CD criticality;
    protected DoseRestriction doseRestriction;
    protected IVLDate proposedAdministrationTimeInterval;
    protected IVLDate validAdministrationTimeInterval;
    protected INT numberFillsAllowed;
}
