package org.opencds.vmr.v1_0.mappings.mappers;

import java.util.ArrayList;
import java.util.List;

import org.opencds.common.utilities.MiscUtility;
import org.opencds.vmr.v1_0.internal.EntityBase;
import org.opencds.vmr.v1_0.internal.RelationshipToSource;
import org.opencds.vmr.v1_0.mappings.utilities.MappingUtility;

public class EntityBaseMapper
{
    public static void pullIn(final org.opencds.vmr.v1_0.schema.EntityBase source, final EntityBase target, final String parentId,
            final org.opencds.vmr.v1_0.schema.CD relationshipToParent, final String subjectPersonId)
    {
        if (source == null)
            return;

        if (source.getEntityType() != null)
            target.setEntityType(MappingUtility.cD2CDInternal(source.getEntityType()));

        if (source.getId() == null)
            target.setId(MiscUtility.getIDAsString());
        else
            target.setId(MappingUtility.iI2FlatId(source.getId()));
        if (source.getTemplateId() != null)
            target.setTemplateId(MappingUtility.iIList2FlatIdList(source.getTemplateId()));

        target.setEvaluatedPersonId(subjectPersonId);

        if ((relationshipToParent != null) && (parentId != null))
        {
            final RelationshipToSource relationshipToSource = new RelationshipToSource();
            final List<RelationshipToSource> relationshipToSources = new ArrayList<>();
            relationshipToSource.setRelationshipToSource(MappingUtility.cD2CDInternal(relationshipToParent));
            relationshipToSource.setSourceId(parentId);
            relationshipToSources.add(relationshipToSource);
            target.setRelationshipToSources(relationshipToSources);
        }

        target.setToBeReturned("EvaluatedPerson".equals(source.getClass().getSimpleName()));

    }

    public static void pushOut(final EntityBase source, final org.opencds.vmr.v1_0.schema.EntityBase target)
    {
        if ((source == null) || ((source.getRelationshipToSources() == null) && (!source.isToBeReturned())))
            return;

        if (source.getEntityType() != null)
            target.setEntityType(MappingUtility.cDInternal2CD(source.getEntityType()));
        if (source.getId() != null)
            target.setId(MappingUtility.iIFlat2II(source.getId()));
        if ((source.getTemplateId() != null) && (source.getTemplateId().length != 0))
            target.getTemplateId().addAll(MappingUtility.iIFlatList2IIList(source.getTemplateId()));
    }
}
