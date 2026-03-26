package org.opencds.vmr.v1_0.mappings.mappers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.opencds.common.exceptions.DataFormatException;
import org.opencds.common.exceptions.ImproperUsageException;
import org.opencds.common.exceptions.InvalidDataException;
import org.opencds.vmr.v1_0.internal.Demographics;
import org.opencds.vmr.v1_0.internal.EvaluatedPerson;
import org.opencds.vmr.v1_0.internal.datatypes.AD;
import org.opencds.vmr.v1_0.internal.datatypes.CD;
import org.opencds.vmr.v1_0.internal.datatypes.EN;
import org.opencds.vmr.v1_0.internal.datatypes.TEL;
import org.opencds.vmr.v1_0.mappings.in.FactLists;
import org.opencds.vmr.v1_0.mappings.utilities.MappingUtility;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class EvaluatedPersonMapper extends EntityBaseMapper
{
    public static EvaluatedPerson pullIn(final org.opencds.vmr.v1_0.schema.EvaluatedPerson external, final EvaluatedPerson internal,
            final String parentId, final org.opencds.vmr.v1_0.schema.CD relationshipToParent, final String subjectPersonId,
            final String focalPersonId, final FactLists factLists)
            throws ImproperUsageException, DataFormatException, InvalidDataException
    {
        final String _METHODNAME = "pullIn(): ";

        if (external == null)
            return null;

        EntityBaseMapper.pullIn(external, internal, parentId, relationshipToParent, subjectPersonId);

        internal.setEvaluatedPersonId(subjectPersonId);
        internal.setFocalPerson(focalPersonId.equals(subjectPersonId));

        if (log.isTraceEnabled())
            log.trace(_METHODNAME + "class {}, {}", external.getClass().getSimpleName(), external.getId());
        if (external.getDemographics() != null)
        {
            if (internal.getDemographics() == null)
                internal.setDemographics(new Demographics());

            if (external.getDemographics().getBirthTime() != null)
                internal.getDemographics().setBirthTime(MappingUtility.tS2DateInternal(external.getDemographics().getBirthTime()));
            if (external.getDemographics().getGender() != null)
                internal.getDemographics().setGender(MappingUtility.cD2CDInternal(external.getDemographics().getGender()));
            else
                internal.getDemographics().setGender(MappingUtility.OPENCDS_NO_INFORMATION);
            if ((external.getDemographics().getRace() != null) && (!external.getDemographics().getRace().isEmpty()))
            {
                internal.getDemographics().setRace(new ArrayList<>());
                for (final org.opencds.vmr.v1_0.schema.CD oneRaceCD : external.getDemographics().getRace())
                    internal.getDemographics().getRace().add(MappingUtility.cD2CDInternal(oneRaceCD));
            }
            if ((external.getDemographics().getEthnicity() != null) && (!external.getDemographics().getEthnicity().isEmpty()))
            {
                internal.getDemographics().setEthnicity(new ArrayList<>());
                for (final org.opencds.vmr.v1_0.schema.CD oneEthnicityCD : external.getDemographics().getEthnicity())
                    internal.getDemographics().getEthnicity().add(MappingUtility.cD2CDInternal(oneEthnicityCD));
            }
            if (external.getDemographics().getPreferredLanguage() != null)
            {
                internal.getDemographics()
                        .setPreferredLanguage(MappingUtility.cD2CDInternal(external.getDemographics().getPreferredLanguage()));
            }
            if (external.getDemographics().getAge() != null)
                internal.getDemographics().setAge(MappingUtility.pQ2PQInternal(external.getDemographics().getAge()));
            if (external.getDemographics().getIsDeceased() != null)
                internal.getDemographics().setIsDeceased(MappingUtility.bL2BLInternal(external.getDemographics().getIsDeceased()));
            if (external.getDemographics().getAgeAtDeath() != null)
                internal.getDemographics().setAgeAtDeath(MappingUtility.pQ2PQInternal(external.getDemographics().getAgeAtDeath()));
            if (external.getDemographics().getName() != null)
            {
                internal.getDemographics().setName(new ArrayList<>());
                for (final org.opencds.vmr.v1_0.schema.EN oneNamePart : external.getDemographics().getName())
                    internal.getDemographics().getName().add(MappingUtility.eN2ENInternal(oneNamePart));
            }
            if (external.getDemographics().getAddress() != null)
            {
                internal.getDemographics().setAddress(new ArrayList<>());
                for (final org.opencds.vmr.v1_0.schema.AD oneAddressPart : external.getDemographics().getAddress())
                    internal.getDemographics().getAddress().add(MappingUtility.aD2ADInternal(oneAddressPart));
            }
            if (external.getDemographics().getTelecom() != null)
            {
                internal.getDemographics().setTelecom(new ArrayList<>());
                for (final org.opencds.vmr.v1_0.schema.TEL oneTelecomPart : external.getDemographics().getTelecom())
                    internal.getDemographics().getTelecom().add(MappingUtility.tEL2TELInternal(oneTelecomPart));
            }

        }

        if (external.getRelatedEntity() != null)
        {
            NestedObjectsMapper.pullInRelatedEntityNestedObjects(external, internal.getId(), subjectPersonId, focalPersonId,
                    factLists);
        }

        return internal;
    }

    public static void pushOut(final Map<String, List<?>> results, final EvaluatedPerson source,
            final org.opencds.vmr.v1_0.schema.CDSOutput output, final String focalPersonId, final String thisEvaluatedPersonId)
            throws DataFormatException, InvalidDataException
    {
        final String _METHODNAME = "pushOut(): ";

        if (source == null)
            return;

        final org.opencds.vmr.v1_0.schema.EvaluatedPerson target = new org.opencds.vmr.v1_0.schema.EvaluatedPerson();

        EntityBaseMapper.pushOut(source, target);

        if (log.isTraceEnabled())
            log.trace(_METHODNAME + "children of {}, {}", source.getClass().getSimpleName(), source.getId());
        if (source.getDemographics() != null)
        {
            if (target.getDemographics() == null)
                target.setDemographics(new org.opencds.vmr.v1_0.schema.EvaluatedPerson.Demographics());

            if (source.getDemographics().getBirthTime() != null)
                target.getDemographics().setBirthTime(MappingUtility.dateInternal2TS(source.getDemographics().getBirthTime()));
            if (source.getDemographics().getGender() != null)
                target.getDemographics().setGender(MappingUtility.cDInternal2CD(source.getDemographics().getGender()));

            if ((source.getDemographics().getRace() != null) && (!source.getDemographics().getRace().isEmpty()))
            {
                for (final CD oneRaceCD : source.getDemographics().getRace())
                    target.getDemographics().getRace().add(MappingUtility.cDInternal2CD(oneRaceCD));
            }

            if ((source.getDemographics().getEthnicity() != null) && (!source.getDemographics().getEthnicity().isEmpty()))
            {
                for (final CD oneEthnicityCD : source.getDemographics().getEthnicity())
                    target.getDemographics().getEthnicity().add(MappingUtility.cDInternal2CD(oneEthnicityCD));
            }

            if (source.getDemographics().getPreferredLanguage() != null)
                target.getDemographics()
                        .setPreferredLanguage(MappingUtility.cDInternal2CD(source.getDemographics().getPreferredLanguage()));
            if (source.getDemographics().getAge() != null)
                target.getDemographics().setAge(MappingUtility.pQInternal2PQ(source.getDemographics().getAge()));
            if (source.getDemographics().getIsDeceased() != null)
                target.getDemographics().setIsDeceased(MappingUtility.bLInternal2BL(source.getDemographics().getIsDeceased()));
            if (source.getDemographics().getAgeAtDeath() != null)
                target.getDemographics().setAgeAtDeath(MappingUtility.pQInternal2PQ(source.getDemographics().getAgeAtDeath()));
            if (source.getDemographics().getName() != null)
            {
                for (final EN oneName : source.getDemographics().getName())
                    target.getDemographics().getName().add(MappingUtility.eNInternal2EN(oneName));
            }
            if (source.getDemographics().getAddress() != null)
            {
                for (final AD oneAddress : source.getDemographics().getAddress())
                    target.getDemographics().getAddress().add(MappingUtility.aDInternal2AD(oneAddress));
            }
            if (source.getDemographics().getTelecom() != null)
            {
                for (final TEL oneTelecom : source.getDemographics().getTelecom())
                    target.getDemographics().getTelecom().add(MappingUtility.tELInternal2TEL(oneTelecom));
            }
        }

        if (focalPersonId.equals(thisEvaluatedPersonId))
            output.getVmrOutput().setPatient(target);
        else
        {
            if (output.getVmrOutput().getOtherEvaluatedPersons() == null)
                output.getVmrOutput().setOtherEvaluatedPersons(new org.opencds.vmr.v1_0.schema.VMR.OtherEvaluatedPersons());
            output.getVmrOutput().getOtherEvaluatedPersons().getEvaluatedPerson().add(target);
        }
    }
}
