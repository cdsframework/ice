package org.opencds.vmr.v1_0.mappings.out.structures;

import java.util.List;
import java.util.Map;

import org.opencds.vmr.v1_0.internal.ClinicalStatement;
import org.opencds.vmr.v1_0.internal.EntityBase;
import org.opencds.vmr.v1_0.internal.EntityRelationship;
import org.opencds.vmr.v1_0.schema.EvaluatedPerson;

public record OrganizedResults(EvaluatedPerson.ClinicalStatements output,
                               String subjectPersonId,
                               String focalPersonId,
                               Map<String, List<?>> results,
                               Map<String, List<ClinicalStatement>> csChildren,
                               Map<String, EntityBase> entityList,
                               Map<String, List<EntityRelationship>> entityChildren)
{
}
