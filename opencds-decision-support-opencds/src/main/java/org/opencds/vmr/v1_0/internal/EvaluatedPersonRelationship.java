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
    private String id;
    private String sourceEntityId;
    private String targetEntityId;
    private CD targetRole;
    private IVLDate relationshipTimeInterval;
}
