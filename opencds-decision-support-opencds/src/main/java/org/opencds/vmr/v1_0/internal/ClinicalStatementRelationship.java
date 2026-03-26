package org.opencds.vmr.v1_0.internal;

import org.opencds.vmr.v1_0.internal.datatypes.CD;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@EqualsAndHashCode
@Getter
@Setter
@ToString
public class ClinicalStatementRelationship
{
    protected String id;
    protected String sourceId;
    protected String targetId;
    protected CD targetRelationshipToSource;
}
