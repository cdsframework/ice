package org.opencds.vmr.v1_0.internal;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@EqualsAndHashCode(callSuper = false)
@ToString
public class Entity extends EntityBase
{
    protected String description;
}
