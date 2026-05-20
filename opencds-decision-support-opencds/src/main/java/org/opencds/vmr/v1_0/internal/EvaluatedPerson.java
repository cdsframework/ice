package org.opencds.vmr.v1_0.internal;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@EqualsAndHashCode(callSuper = false)
@Getter
@Setter
@ToString
public class EvaluatedPerson extends EntityBase
{
    private Demographics demographics;
    private boolean focalPerson;
}
