package org.opencds.vmr.v1_0.internal;

import org.opencds.vmr.v1_0.internal.datatypes.CD;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@EqualsAndHashCode(callSuper = false)
@Getter
@Setter
@ToString
public abstract class ObservationBase extends ClinicalStatement
{
    private CD observationFocus;
    private CD observationMethod;
    private BodySite targetBodySite;
}
