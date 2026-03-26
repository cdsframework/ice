package org.opencds.vmr.v1_0.mappings.mappers;

import org.opencds.common.exceptions.DataFormatException;
import org.opencds.common.exceptions.ImproperUsageException;
import org.opencds.common.exceptions.InvalidDataException;
import org.opencds.vmr.v1_0.internal.Entity;
import org.opencds.vmr.v1_0.mappings.in.FactLists;
import org.opencds.vmr.v1_0.mappings.out.structures.OrganizedResults;
import org.opencds.vmr.v1_0.mappings.utilities.MappingUtility;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class EntityMapper extends EntityBaseMapper
{
    public static Entity pullIn(final org.opencds.vmr.v1_0.schema.Entity source, final Entity target, final String parentId,
            final org.opencds.vmr.v1_0.schema.CD relationshipToParent, final String subjectPersonId, final String focalPersonId,
            final FactLists factLists) throws ImproperUsageException, DataFormatException, InvalidDataException
    {
        final String _METHODNAME = "pullIn(): ";

        if (source == null)
            return null;

        if (log.isTraceEnabled())
            log.trace(_METHODNAME + "{}, {}", source.getClass().getSimpleName(), source.getId());
        EntityBaseMapper.pullIn(source, target, parentId, relationshipToParent, subjectPersonId);
        if (source.getDescription() != null)
            target.setDescription(MappingUtility.sT2STInternal(source.getDescription()));

        factLists.put(Entity.class, target);

        if (source.getRelatedEntity() != null)
            NestedObjectsMapper.pullInRelatedEntityNestedObjects(source, target.getId(), subjectPersonId, focalPersonId, factLists);

        return target;
    }

    public static org.opencds.vmr.v1_0.schema.Entity pushOut(final Entity source, final org.opencds.vmr.v1_0.schema.Entity target,
            final OrganizedResults organizedResults) throws ImproperUsageException, DataFormatException, InvalidDataException
    {
        final String _METHODNAME = "pushOut(): ";

        if (source == null)
            return null;

        if (log.isTraceEnabled())
            log.trace(_METHODNAME + "{}, {}", source.getClass().getSimpleName(), source.getId());
        EntityBaseMapper.pushOut(source, target);

        if (source.getDescription() != null)
            target.setDescription(MappingUtility.sTInternal2ST(source.getDescription()));

        NestedObjectsMapper.pushOutRelatedEntityNestedObjects(source.getId(), target, organizedResults);

        return target;
    }
}
