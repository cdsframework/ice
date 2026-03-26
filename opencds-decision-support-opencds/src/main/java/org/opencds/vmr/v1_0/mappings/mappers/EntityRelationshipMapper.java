package org.opencds.vmr.v1_0.mappings.mappers;

import org.opencds.common.exceptions.OpenCDSRuntimeException;
import org.opencds.vmr.v1_0.internal.EntityRelationship;
import org.opencds.vmr.v1_0.mappings.in.FactLists;
import org.opencds.vmr.v1_0.mappings.utilities.MappingUtility;

public class EntityRelationshipMapper
{
    public static void pullIn(final org.opencds.vmr.v1_0.schema.II sourceId, final org.opencds.vmr.v1_0.schema.II targetId,
            final org.opencds.vmr.v1_0.schema.CD targetRelationshipToSource,
            final org.opencds.vmr.v1_0.schema.IVLTS relationshipTimeInterval, final FactLists factLists)
    {
        pullIn(MappingUtility.iI2FlatId(sourceId), targetId, targetRelationshipToSource, relationshipTimeInterval, factLists);
    }

    public static void pullIn(final String sourceId, final org.opencds.vmr.v1_0.schema.II targetId,
            final org.opencds.vmr.v1_0.schema.CD targetRelationshipToSource,
            final org.opencds.vmr.v1_0.schema.IVLTS relationshipTimeInterval, final FactLists factLists)
    {
        if (sourceId == null)
            throw new OpenCDSRuntimeException("sourceId of EntityRelationship must not be null");
        if (targetId == null)
            throw new OpenCDSRuntimeException("targetEntityId of EntityRelationship must not be null");
        final String targetIdString = MappingUtility.iI2FlatId(targetId);

        if (targetIdString.equals(sourceId))
        {
            throw new OpenCDSRuntimeException(
                    "root and/or extension of source and target IDs of EntityRelationship may not be the same: source (root^extension)= "
                            + sourceId + ", target (root^extension)= " + targetIdString);
        }
        final EntityRelationship target = new EntityRelationship();
        target.setId(MappingUtility.getUUIDAsII().getValue());
        target.setSourceId(sourceId);
        target.setTargetEntityId(targetIdString);
        target.setTargetRole(MappingUtility.cD2CDInternal(targetRelationshipToSource));
        target.setRelationshipTimeInterval(MappingUtility.iVLTS2IVLDateInternal(relationshipTimeInterval));
        factLists.put(EntityRelationship.class, target);
    }

    public static org.opencds.vmr.v1_0.schema.RelatedEntity pushOut(final EntityRelationship source)
    {
        if (source == null)
            return null;

        final org.opencds.vmr.v1_0.schema.RelatedEntity target = new org.opencds.vmr.v1_0.schema.RelatedEntity();

        target.setTargetRole(MappingUtility.cDInternal2CD(source.getTargetRole()));
        target.setRelationshipTimeInterval(MappingUtility.iVLDateInternal2IVLTS(source.getRelationshipTimeInterval()));
        return target;
    }
}
