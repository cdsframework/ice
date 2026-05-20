package org.opencds.vmr.v1_0.internal.datatypes;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@EqualsAndHashCode(callSuper = false)
@Getter
@Setter
@ToString
public class RTO extends QTY
{
    private double numerator;
    private double denominator;
}
