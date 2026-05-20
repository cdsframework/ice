package org.opencds.vmr.v1_0.mappings.mappers;

import java.time.LocalDate;
import java.util.Map;

import org.opencds.common.exceptions.OpenCDSRuntimeException;
import org.opencds.vmr.v1_0.internal.EvaluatedPersonRelationship;
import org.opencds.vmr.v1_0.mappings.utilities.MappingUtility;

public class EvaluatedPersonRelationshipMapper
{
    public static EvaluatedPersonRelationship pullIn(final org.opencds.vmr.v1_0.schema.EntityRelationship external,
            final Map<String, LocalDate> parsedDatesCache)
    {
        if (external == null)
            return null;

        if (external.getSourceId() == null)
            throw new OpenCDSRuntimeException("sourceId of EvaluatedPersonRelationship must not be null");
        if (external.getTargetEntityId() == null)
            throw new OpenCDSRuntimeException("targetEntityId of EvaluatedPersonRelationship must not be null");
        final String sourceId = MappingUtility.iI2FlatId(external.getSourceId());
        final String targetId = MappingUtility.iI2FlatId(external.getTargetEntityId());
        if (targetId.equals(sourceId))
        {
            throw new OpenCDSRuntimeException(
                    "root and extension of source and target ID of EvaluatedPersonRelationship may not be the same: source (root^extension)= "
                            + sourceId + ", target (root^extension)= " + targetId);
        }
        final EvaluatedPersonRelationship internal = new EvaluatedPersonRelationship();
        internal.setId(MappingUtility.getUUIDAsII().getValue());
        internal.setSourceEntityId(sourceId);
        internal.setTargetEntityId(targetId);
        internal.setTargetRole(MappingUtility.cD2CDInternal(external.getTargetRole()));
        internal.setRelationshipTimeInterval(
                MappingUtility.iVLTS2IVLDateInternal(external.getRelationshipTimeInterval(), parsedDatesCache));

        return internal;
    }

    public static org.opencds.vmr.v1_0.schema.EntityRelationship pushOut(final EvaluatedPersonRelationship source)
    {
        if (source == null)
            return null;

        final org.opencds.vmr.v1_0.schema.EntityRelationship target = new org.opencds.vmr.v1_0.schema.EntityRelationship();
        target.setSourceId(MappingUtility.iIFlat2II(source.getSourceEntityId()));
        target.setTargetEntityId(MappingUtility.iIFlat2II(source.getTargetEntityId()));
        target.setTargetRole(MappingUtility.cDInternal2CD(source.getTargetRole()));
        target.setRelationshipTimeInterval(MappingUtility.iVLDateInternal2IVLTS(source.getRelationshipTimeInterval()));

        return target;
    }
}
