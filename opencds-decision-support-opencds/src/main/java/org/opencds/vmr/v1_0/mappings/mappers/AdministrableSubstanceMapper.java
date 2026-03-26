package org.opencds.vmr.v1_0.mappings.mappers;

import org.opencds.common.exceptions.DataFormatException;
import org.opencds.common.exceptions.ImproperUsageException;
import org.opencds.common.exceptions.InvalidDataException;
import org.opencds.vmr.v1_0.internal.AdministrableSubstance;
import org.opencds.vmr.v1_0.mappings.in.FactLists;
import org.opencds.vmr.v1_0.mappings.out.structures.OrganizedResults;
import org.opencds.vmr.v1_0.mappings.utilities.MappingUtility;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AdministrableSubstanceMapper extends EntityBaseMapper
{
    public static AdministrableSubstance pullIn(final org.opencds.vmr.v1_0.schema.AdministrableSubstance external,
            final AdministrableSubstance internal, final String parentId, final org.opencds.vmr.v1_0.schema.CD relationshipToParent,
            final String subjectPersonId, final String focalPersonId, final FactLists factLists)
            throws ImproperUsageException, DataFormatException, InvalidDataException
    {
        final String _METHODNAME = "pullIn(): ";
        if (log.isTraceEnabled())
            log.trace(_METHODNAME + "{}, {}, {}", external.getClass().getSimpleName(), MappingUtility.iI2FlatId(external.getId()),
                    parentId);

        EntityBaseMapper.pullIn(external, internal, parentId, relationshipToParent, subjectPersonId);

        if (external.getSubstanceCode() != null)
            internal.setSubstanceCode(MappingUtility.cD2CDInternal(external.getSubstanceCode()));
        if (external.getStrength() != null)
            internal.setStrength(MappingUtility.rTO2RTOInternal(external.getStrength()));
        if (external.getForm() != null)
            internal.setForm(MappingUtility.cD2CDInternal(external.getForm()));
        if (external.getSubstanceBrandCode() != null)
            internal.setSubstanceBrandCode(MappingUtility.cD2CDInternal(external.getSubstanceBrandCode()));
        if (external.getSubstanceGenericCode() != null)
            internal.setSubstanceGenericCode(MappingUtility.cD2CDInternal(external.getSubstanceGenericCode()));
        if (external.getManufacturer() != null)
            internal.setManufacturer(MappingUtility.cD2CDInternal(external.getManufacturer()));
        if (external.getLotNo() != null)
            internal.setLotNo(MappingUtility.sT2STInternal(external.getLotNo()));

        factLists.put(AdministrableSubstance.class, internal);

        if (external.getRelatedEntity() != null)
        {
            NestedObjectsMapper.pullInRelatedEntityNestedObjects(external, internal.getId(), subjectPersonId, focalPersonId,
                    factLists);
        }

        return internal;
    }

    public static org.opencds.vmr.v1_0.schema.AdministrableSubstance pushOut(final AdministrableSubstance source,
            final org.opencds.vmr.v1_0.schema.AdministrableSubstance target, final OrganizedResults organizedResults)
            throws ImproperUsageException, DataFormatException, InvalidDataException
    {
        final String _METHODNAME = "pushOut(): ";

        if (source == null)
            return null;

        if (log.isTraceEnabled())
            log.trace(_METHODNAME + "{}, {}", source.getClass().getSimpleName(), source.getId());
        EntityBaseMapper.pushOut(source, target);

        if (source.getSubstanceCode() != null)
            target.setSubstanceCode(MappingUtility.cDInternal2CD(source.getSubstanceCode()));
        if (source.getStrength() != null)
            target.setStrength(MappingUtility.rTOInternal2RTO(source.getStrength()));
        if (source.getForm() != null)
            target.setForm(MappingUtility.cDInternal2CD(source.getForm()));
        if (source.getSubstanceBrandCode() != null)
            target.setSubstanceBrandCode(MappingUtility.cDInternal2CD(source.getSubstanceBrandCode()));
        if (source.getSubstanceGenericCode() != null)
            target.setSubstanceGenericCode(MappingUtility.cDInternal2CD(source.getSubstanceGenericCode()));
        if (source.getManufacturer() != null)
            target.setManufacturer(MappingUtility.cDInternal2CD(source.getManufacturer()));
        if (source.getLotNo() != null)
            target.setLotNo(MappingUtility.sTInternal2ST(source.getLotNo()));

        NestedObjectsMapper.pushOutRelatedEntityNestedObjects(source.getId(), target, organizedResults);

        return target;

    }

    public static org.opencds.vmr.v1_0.schema.AdministrableSubstance pushOut(final AdministrableSubstance source,
            final org.opencds.vmr.v1_0.schema.AdministrableSubstance target)
    {
        final String _METHODNAME = "pushOut(): ";

        if (source == null)
            return null;

        if (log.isTraceEnabled())
            log.trace(_METHODNAME + "{}, {}", source.getClass().getSimpleName(), source.getId());
        EntityBaseMapper.pushOut(source, target);

        if (source.getSubstanceCode() != null)
            target.setSubstanceCode(MappingUtility.cDInternal2CD(source.getSubstanceCode()));
        if (source.getStrength() != null)
            target.setStrength(MappingUtility.rTOInternal2RTO(source.getStrength()));
        if (source.getForm() != null)
            target.setForm(MappingUtility.cDInternal2CD(source.getForm()));
        if (source.getSubstanceBrandCode() != null)
            target.setSubstanceBrandCode(MappingUtility.cDInternal2CD(source.getSubstanceBrandCode()));
        if (source.getSubstanceGenericCode() != null)
            target.setSubstanceGenericCode(MappingUtility.cDInternal2CD(source.getSubstanceGenericCode()));
        if (source.getManufacturer() != null)
            target.setManufacturer(MappingUtility.cDInternal2CD(source.getManufacturer()));
        if (source.getLotNo() != null)
            target.setLotNo(MappingUtility.sTInternal2ST(source.getLotNo()));

        return target;
    }
}
