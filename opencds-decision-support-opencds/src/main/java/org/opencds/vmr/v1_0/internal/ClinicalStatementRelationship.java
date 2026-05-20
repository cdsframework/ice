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
    private String id;
    private String sourceId;
    private String targetId;
    private CD targetRelationshipToSource;
}
