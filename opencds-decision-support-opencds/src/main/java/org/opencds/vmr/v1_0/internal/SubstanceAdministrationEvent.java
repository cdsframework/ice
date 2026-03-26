package org.opencds.vmr.v1_0.internal;

import org.opencds.vmr.v1_0.internal.datatypes.BL;
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
public class SubstanceAdministrationEvent extends SubstanceAdministrationBase
{
    protected INT doseNumber;
    protected IVLDate administrationTimeInterval;
    protected IVLDate documentationTime;
    protected CD informationAttestationType;
    protected BL isValid;
}
