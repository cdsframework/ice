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
    private CD substanceCode;
    private RTO strength;
    private CD form;
    private CD substanceBrandCode;
    private CD substanceGenericCode;
    private CD manufacturer;
    private String lotNo;
}/**/
