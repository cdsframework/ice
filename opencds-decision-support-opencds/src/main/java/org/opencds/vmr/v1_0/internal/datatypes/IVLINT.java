package org.opencds.vmr.v1_0.internal.datatypes;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@EqualsAndHashCode(callSuper = false)
@Getter
@Setter
@ToString
public class IVLINT extends ANY
{
    private int low;
    private int high;
    private Boolean lowIsInclusive;
    private Boolean highIsInclusive;

    public Boolean isLowIsInclusive()
    {
        return lowIsInclusive;
    }

    public Boolean isHighIsInclusive()
    {
        return highIsInclusive;
    }
}
