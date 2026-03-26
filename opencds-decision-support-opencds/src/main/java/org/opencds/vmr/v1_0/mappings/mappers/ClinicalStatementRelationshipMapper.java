package org.opencds.vmr.v1_0.mappings.mappers;

import org.opencds.common.exceptions.OpenCDSRuntimeException;
import org.opencds.vmr.v1_0.internal.ClinicalStatementRelationship;
import org.opencds.vmr.v1_0.internal.datatypes.CD;
import org.opencds.vmr.v1_0.mappings.in.FactLists;
import org.opencds.vmr.v1_0.mappings.utilities.MappingUtility;

public class ClinicalStatementRelationshipMapper
{
    public static void pullIn(final org.opencds.vmr.v1_0.schema.ClinicalStatementRelationship external, final FactLists factLists)
    {
        if (external == null)
            return;

        final ClinicalStatementRelationship internal = new ClinicalStatementRelationship();
        if (external.getSourceId() != null)
            internal.setId(MappingUtility.getUUIDAsII().getValue());
        final String sourceId = MappingUtility.iI2FlatId(external.getSourceId());
        final String targetId = MappingUtility.iI2FlatId(external.getTargetId());
        if (targetId.equals(sourceId))
        {
            throw new OpenCDSRuntimeException(
                    "root and extension of source and target ID of ClinicalStatementRelationship may not be the same: source (root^extension)= "
                            + sourceId + ", target (root^extension)= " + targetId);
        }
        if (external.getSourceId() != null)
            internal.setSourceId(sourceId);
        if (external.getTargetId() != null)
            internal.setTargetId(targetId);
        if (external.getTargetRelationshipToSource() != null)
            internal.setTargetRelationshipToSource(MappingUtility.cD2CDInternal(external.getTargetRelationshipToSource()));
        factLists.put(ClinicalStatementRelationship.class, internal);

    }

    public static void pullIn(final String sourceId, final String targetId, final CD targetRelationshipToSource,
            final FactLists factLists)
    {
        if (targetId.equals(sourceId))
        {
            throw new OpenCDSRuntimeException(
                    "root and extension of source and target ID of ClinicalStatementRelationship may not be the same: source (root^extension)= "
                            + sourceId + ", target (root^extension)= " + targetId);
        }
        final ClinicalStatementRelationship target = new ClinicalStatementRelationship();
        target.setId(MappingUtility.getUUIDAsII().getValue());
        target.setSourceId(sourceId);
        target.setTargetId(targetId);
        target.setTargetRelationshipToSource(targetRelationshipToSource);
        factLists.put(ClinicalStatementRelationship.class, target);

    }

    public static org.opencds.vmr.v1_0.schema.ClinicalStatementRelationship pushOut(final ClinicalStatementRelationship source)
    {
        if (source == null)
            return null;

        final org.opencds.vmr.v1_0.schema.ClinicalStatementRelationship target =
                new org.opencds.vmr.v1_0.schema.ClinicalStatementRelationship();
        if (source.getSourceId() != null)
            target.setSourceId(MappingUtility.iIFlat2II(source.getSourceId()));
        if (source.getTargetId() != null)
            target.setTargetId(MappingUtility.iIFlat2II(source.getTargetId()));
        if (source.getTargetRelationshipToSource() != null)
            target.setTargetRelationshipToSource(MappingUtility.cDInternal2CD(source.getTargetRelationshipToSource()));
        return target;
    }
}
