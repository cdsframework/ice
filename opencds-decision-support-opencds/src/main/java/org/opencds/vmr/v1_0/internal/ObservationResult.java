package org.opencds.vmr.v1_0.internal;

import java.util.List;

import org.opencds.vmr.v1_0.internal.datatypes.CD;
import org.opencds.vmr.v1_0.internal.datatypes.IVLDate;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@EqualsAndHashCode(callSuper = false)
@Getter
@Setter
@ToString
public class ObservationResult extends ObservationBase
{
    private IVLDate observationEventTime;
    private ObservationValue observationValue;
    private List<CD> interpretation;
}
