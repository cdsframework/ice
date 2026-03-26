package org.opencds.vmr.v1_0.internal;

import org.opencds.vmr.v1_0.internal.datatypes.CD;
import org.opencds.vmr.v1_0.internal.datatypes.RTO;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@EqualsAndHashCode(callSuper = false)
@Getter
@Setter
@ToString
public class AdministrableSubstance extends EntityBase
{
    protected CD substanceCode;
    protected RTO strength;
    protected CD form;
    protected CD substanceBrandCode;
    protected CD substanceGenericCode;
    protected CD manufacturer;
    protected String lotNo;
}
