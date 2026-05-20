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
public abstract class ClinicalStatement
{
    private String[] templateId;
    private String id;
    private CD dataSourceType;
    private String evaluatedPersonId;
    private boolean subjectIsFocalPerson;
    private boolean clinicalStatementToBeRoot;
    private boolean toBeReturned;
    private List<RelationshipToSource> relationshipToSources;
}
