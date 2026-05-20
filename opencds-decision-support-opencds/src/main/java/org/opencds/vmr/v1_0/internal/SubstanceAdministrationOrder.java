package org.opencds.vmr.v1_0.internal;

import java.util.List;

import org.opencds.vmr.v1_0.internal.datatypes.CD;
import org.opencds.vmr.v1_0.internal.datatypes.INT;
import org.opencds.vmr.v1_0.internal.datatypes.IVLDate;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@EqualsAndHashCode(callSuper = false)
@ToString
public class SubstanceAdministrationOrder extends SubstanceAdministrationBase
{
    private CD criticality;
    private DoseRestriction doseRestriction;
    private IVLDate administrationTimeInterval;
    private List<CD> dosingSig;
    private INT numberFillsAllowed;
    private IVLDate orderEventTime;
}
