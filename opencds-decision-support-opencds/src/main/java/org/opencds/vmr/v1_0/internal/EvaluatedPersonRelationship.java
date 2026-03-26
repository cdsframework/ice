package org.opencds.vmr.v1_0.internal;

import org.opencds.vmr.v1_0.internal.datatypes.CD;
import org.opencds.vmr.v1_0.internal.datatypes.IVLDate;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@EqualsAndHashCode
@ToString
public class EvaluatedPersonRelationship
{
    protected String id;
    protected String sourceEntityId;
    protected String targetEntityId;
    protected CD targetRole;
    protected IVLDate relationshipTimeInterval;
}
