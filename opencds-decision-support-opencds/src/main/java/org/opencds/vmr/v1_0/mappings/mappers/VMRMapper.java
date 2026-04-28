package org.opencds.vmr.v1_0.mappings.mappers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.opencds.common.exceptions.DataFormatException;
import org.opencds.common.exceptions.InvalidDataException;
import org.opencds.common.exceptions.OpenCDSRuntimeException;
import org.opencds.vmr.v1_0.internal.EvaluatedPerson;
import org.opencds.vmr.v1_0.internal.VMR;
import org.opencds.vmr.v1_0.mappings.utilities.MappingUtility;

public class VMRMapper
{
    public static VMR pullIn(final org.opencds.vmr.v1_0.schema.VMR source, final VMR target)
    {
        if (source == null)
            return null;

        final List<String> templateIds = new ArrayList<>();
        for (int i = 0; i < source.getTemplateId().size(); i++)
            templateIds.add(MappingUtility.iI2Root("templateId", source.getTemplateId().get(i)));
        if (!templateIds.isEmpty())
            target.setTemplateId(templateIds);
        return target;
    }

    public static void pushOut(final Map<String, List<?>> results, final VMR source,
            final org.opencds.vmr.v1_0.schema.CDSOutput output, final String focalPersonId)
            throws DataFormatException, InvalidDataException
    {
        if (source == null)
            return;

        final org.opencds.vmr.v1_0.schema.VMR target = new org.opencds.vmr.v1_0.schema.VMR();
        if (source.getTemplateId() != null)
        {
            for (final String oneTemplateId : source.getTemplateId())
                target.getTemplateId().add(MappingUtility.iIFlat2II(oneTemplateId));
            target.setPatient(new org.opencds.vmr.v1_0.schema.EvaluatedPerson());

        }
        else
        {
            throw new OpenCDSRuntimeException(
                    "No templateId(s) for input EvaluatedPerson found by CdsOutputResultSetBuilder.  No result will be returned.");
        }

        output.setVmrOutput(target);
        if (results.get("EvaluatedPerson") != null)
        {
            @SuppressWarnings("unchecked")
            final List<EvaluatedPerson> sourceEvaluatedPersonList = (List<EvaluatedPerson>) results.get("EvaluatedPerson");
            for (final EvaluatedPerson sourceEvaluatedPerson : sourceEvaluatedPersonList)
            {
                final String thisEvaluatedPersonId = sourceEvaluatedPerson.getId();
                EvaluatedPersonMapper.pushOut(results, sourceEvaluatedPerson, output, focalPersonId, thisEvaluatedPersonId);
            }
        }
        else
        {
            throw new OpenCDSRuntimeException(
                    "No output EvaluatedPerson found by CdsOutputResultSetBuilder.  No result will be returned.");
        }
    }
}
