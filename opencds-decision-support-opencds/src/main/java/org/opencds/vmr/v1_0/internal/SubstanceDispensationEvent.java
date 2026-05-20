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
    private INT daysSupply;
    private PQ dispensationQuantity;
    private DoseRestriction doseRestriction;
    private IVLDate dispensationTime;
    private INT fillNumber;
    private INT fillsRemaining;
}
