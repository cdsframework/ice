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
    protected String[] templateId;
    protected String id;
    protected CD entityType;

    protected String evaluatedPersonId;

    protected boolean toBeReturned;
    protected List<RelationshipToSource> relationshipToSources;
}
