package org.opencds.vmr.v1_0.internal.datatypes;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@EqualsAndHashCode(callSuper = false)
@Getter
@Setter
@ToString
public class IVLPQ extends ANY
{
    private String lowUnit;
    private double lowValue;
    private String highUnit;
    private double highValue;
    private Boolean lowIsInclusive;
    private Boolean highIsInclusive;
}
