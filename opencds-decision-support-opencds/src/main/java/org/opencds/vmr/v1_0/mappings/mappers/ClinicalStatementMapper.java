package org.opencds.vmr.v1_0.mappings.mappers;

import org.opencds.common.exceptions.ImproperUsageException;
import org.opencds.common.exceptions.OpenCDSRuntimeException;
import org.opencds.vmr.v1_0.internal.ClinicalStatement;
import org.opencds.vmr.v1_0.mappings.in.FactLists;
import org.opencds.vmr.v1_0.mappings.utilities.MappingUtility;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class ClinicalStatementMapper
{
    public static void pullIn(final org.opencds.vmr.v1_0.schema.ClinicalStatement external, final ClinicalStatement internal,
            final String subjectPersonId, final String focalPersonId, final FactLists factLists) throws ImproperUsageException
    {
        final String _METHODNAME = "pullIn(): ";

        if (external == null)
            return;
        if (internal == null)
        {
            final String errStr = _METHODNAME + "improper usage: target supplied is null";
            log.error(errStr);
            throw new ImproperUsageException(errStr);
        }
        if (external.getId() == null)
            throw new OpenCDSRuntimeException("source ID of ClinicalStatement must not be null");
        if (external.getTemplateId() != null)
            internal.setTemplateId(MappingUtility.iIList2FlatIdList(external.getTemplateId()));
        internal.setId(MappingUtility.iI2FlatId(external.getId()));
        if (external.getDataSourceType() != null)
            internal.setDataSourceType(MappingUtility.cD2CDInternal(external.getDataSourceType()));
        internal.setEvaluatedPersonId(subjectPersonId);
        internal.setSubjectIsFocalPerson((subjectPersonId.equals(focalPersonId)));
        internal.setClinicalStatementToBeRoot(true);

        internal.setToBeReturned(false);
    }

    public static void pushOut(final ClinicalStatement source, final org.opencds.vmr.v1_0.schema.ClinicalStatement target)
            throws ImproperUsageException
    {
        final String _METHODNAME = "pushOut(): ";

        if ((source == null) || (!source.isToBeReturned()))
            return;

        if (target == null)
        {
            final String errStr = _METHODNAME + "improper usage: target supplied is null";
            log.error(errStr);
            throw new ImproperUsageException(errStr);
        }

        if ((source.getTemplateId() != null) && (source.getTemplateId().length != 0))
            target.getTemplateId().addAll(MappingUtility.iIFlatList2IIList(source.getTemplateId()));
        target.setId(MappingUtility.iIFlat2II(source.getId()));
        if (source.getDataSourceType() != null)
            target.setDataSourceType(MappingUtility.cDInternal2CD(source.getDataSourceType()));
        if (source.getId() != null)
            target.setId(MappingUtility.iIFlat2II(source.getId()));
    }
}
