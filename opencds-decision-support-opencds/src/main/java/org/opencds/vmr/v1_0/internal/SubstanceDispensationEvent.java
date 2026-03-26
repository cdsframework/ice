package org.opencds.vmr.v1_0.internal;

import org.opencds.vmr.v1_0.internal.datatypes.INT;
import org.opencds.vmr.v1_0.internal.datatypes.IVLDate;
import org.opencds.vmr.v1_0.internal.datatypes.PQ;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@EqualsAndHashCode(callSuper = false)
@ToString
public class SubstanceDispensationEvent extends SubstanceAdministrationBase
{
    protected INT daysSupply;
    protected PQ dispensationQuantity;
    protected DoseRestriction doseRestriction;
    protected IVLDate dispensationTime;
    protected INT fillNumber;
    protected INT fillsRemaining;
}
