package org.opencds.vmr.v1_0.internal.datatypes;

import java.time.LocalDate;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@EqualsAndHashCode(callSuper = false)
@Getter
@Setter
@ToString
public class IVLDate extends ANY
{
    private LocalDate low;
    private LocalDate high;
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
