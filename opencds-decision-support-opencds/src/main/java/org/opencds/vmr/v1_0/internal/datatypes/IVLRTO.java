package org.opencds.vmr.v1_0.internal.datatypes;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@EqualsAndHashCode(callSuper = false)
@Getter
@Setter
@ToString
public class IVLRTO extends ANY
{
    protected double lowNumerator;
    protected double lowDenominator;
    protected double highNumerator;
    protected double highDenominator;
    protected Boolean lowIsInclusive;
    protected Boolean highIsInclusive;
}
