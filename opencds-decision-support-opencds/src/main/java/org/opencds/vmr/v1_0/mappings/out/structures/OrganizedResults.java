package org.opencds.vmr.v1_0.mappings.out.structures;

import java.util.List;
import java.util.Map;

import org.opencds.vmr.v1_0.internal.ClinicalStatement;
import org.opencds.vmr.v1_0.internal.EntityBase;
import org.opencds.vmr.v1_0.internal.EntityRelationship;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class OrganizedResults
{
    protected org.opencds.vmr.v1_0.schema.EvaluatedPerson.ClinicalStatements output;
    protected String subjectPersonId;
    protected String focalPersonId;
    protected Map<String, List<?>> results;
    protected Map<String, List<ClinicalStatement>> csChildren;

    protected Map<String, EntityBase> entityList;
    protected Map<String, List<EntityRelationship>> entityChildren;
}
