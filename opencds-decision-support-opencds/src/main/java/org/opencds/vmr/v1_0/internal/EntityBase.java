package org.opencds.vmr.v1_0.internal;

import java.util.List;

import org.opencds.vmr.v1_0.internal.datatypes.CD;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@EqualsAndHashCode
@Getter
@Setter
@ToString
public class EntityBase
{
    private String[] templateId;
    private String id;
    private CD entityType;

    private String evaluatedPersonId;

    private boolean toBeReturned;
    private List<RelationshipToSource> relationshipToSources;
}
