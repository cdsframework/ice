package org.opencds.vmr.v1_0.mappings.mappers;

import java.util.ArrayList;

import org.opencds.common.exceptions.DataFormatException;
import org.opencds.common.exceptions.ImproperUsageException;
import org.opencds.common.exceptions.InvalidDataException;
import org.opencds.vmr.v1_0.internal.Organization;
import org.opencds.vmr.v1_0.internal.datatypes.AD;
import org.opencds.vmr.v1_0.internal.datatypes.EN;
import org.opencds.vmr.v1_0.internal.datatypes.TEL;
import org.opencds.vmr.v1_0.mappings.in.FactLists;
import org.opencds.vmr.v1_0.mappings.out.structures.OrganizedResults;
import org.opencds.vmr.v1_0.mappings.utilities.MappingUtility;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class OrganizationMapper extends EntityBaseMapper
{
    public static Organization pullIn(final org.opencds.vmr.v1_0.schema.Organization source, final Organization target,
            final String parentId, final org.opencds.vmr.v1_0.schema.CD relationshipToParent, final String subjectPersonId,
            final String focalPersonId, final FactLists factLists)
            throws ImproperUsageException, DataFormatException, InvalidDataException
    {
        final String _METHODNAME = "pullIn(): ";

        if (source == null)
            return null;

        if (log.isTraceEnabled())
            log.trace(_METHODNAME + "{}, {}", source.getClass().getSimpleName(), source.getId());
        EntityBaseMapper.pullIn(source, target, parentId, relationshipToParent, subjectPersonId);

        if (source.getName() != null)
        {
            target.setName(new ArrayList<>());
            for (final org.opencds.vmr.v1_0.schema.EN oneNamePart : source.getName())
                target.getName().add(MappingUtility.eN2ENInternal(oneNamePart));
        }
        if (source.getAddress() != null)
        {
            target.setAddress(new ArrayList<>());
            for (final org.opencds.vmr.v1_0.schema.AD oneAddressPart : source.getAddress())
                target.getAddress().add(MappingUtility.aD2ADInternal(oneAddressPart));
        }
        if (source.getTelecom() != null)
        {
            target.setTelecom(new ArrayList<>());
            for (final org.opencds.vmr.v1_0.schema.TEL oneTelecomPart : source.getTelecom())
                target.getTelecom().add(MappingUtility.tEL2TELInternal(oneTelecomPart));
        }

        factLists.put(Organization.class, target);

        if (source.getRelatedEntity() != null)
            NestedObjectsMapper.pullInRelatedEntityNestedObjects(source, target.getId(), subjectPersonId, focalPersonId, factLists);

        return target;
    }

    public static org.opencds.vmr.v1_0.schema.Organization pushOut(final Organization source,
            final org.opencds.vmr.v1_0.schema.Organization target, final OrganizedResults organizedResults)
            throws ImproperUsageException, DataFormatException, InvalidDataException
    {
        final String _METHODNAME = "pushOut(): ";

        if (source == null)
            return null;

        if (log.isTraceEnabled())
            log.trace(_METHODNAME + "children of {}, {}", source.getClass().getSimpleName(), source.getId());
        EntityBaseMapper.pushOut(source, target);

        if (source.getName() != null)
        {
            for (final EN oneName : source.getName())
                target.getName().add(MappingUtility.eNInternal2EN(oneName));
        }
        if (source.getAddress() != null)
        {
            for (final AD oneAddress : source.getAddress())
                target.getAddress().add(MappingUtility.aDInternal2AD(oneAddress));
        }
        if (source.getTelecom() != null)
        {
            for (final TEL oneTelecom : source.getTelecom())
                target.getTelecom().add(MappingUtility.tELInternal2TEL(oneTelecom));
        }

        NestedObjectsMapper.pushOutRelatedEntityNestedObjects(source.getId(), target, organizedResults);

        return target;
    }
}
