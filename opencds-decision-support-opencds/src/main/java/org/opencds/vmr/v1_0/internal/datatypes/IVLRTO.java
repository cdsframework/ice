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
    private double lowNumerator;
    private double lowDenominator;
    private double highNumerator;
    private double highDenominator;
    private Boolean lowIsInclusive;
    private Boolean highIsInclusive;
}
