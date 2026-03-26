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
    protected String lowUnit;
    protected double lowValue;
    protected String highUnit;
    protected double highValue;
    protected Boolean lowIsInclusive;
    protected Boolean highIsInclusive;
}
