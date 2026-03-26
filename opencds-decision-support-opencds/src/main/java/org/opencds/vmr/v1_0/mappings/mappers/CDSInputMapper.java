package org.opencds.vmr.v1_0.mappings.mappers;

import java.util.ArrayList;
import java.util.List;

import org.opencds.vmr.v1_0.internal.CDSContext;
import org.opencds.vmr.v1_0.internal.CDSInput;
import org.opencds.vmr.v1_0.internal.CDSResource;
import org.opencds.vmr.v1_0.mappings.utilities.MappingUtility;

public class CDSInputMapper
{
    public static void pullIn(final org.opencds.vmr.v1_0.schema.CDSInput source, final CDSInput target)
    {
        if (source == null)
            return;

        final List<String> templateIds = new ArrayList<>();
        for (int i = 0; i < source.getTemplateId().size(); i++)
            templateIds.add(MappingUtility.iI2Root("templateId", source.getTemplateId().get(i)));
        if (!templateIds.isEmpty())
            target.setTemplateId(templateIds);
        final List<CDSResource> cdsResources = new ArrayList<>();
        for (final org.opencds.vmr.v1_0.schema.CDSResource thisCdsResource : source.getCdsResource())
        {
            if (thisCdsResource != null)
            {
                final CDSResource targetCDSResource = new CDSResource();

                if (thisCdsResource.getCdsResourceType() != null)
                    targetCDSResource.setCdsResourceType(MappingUtility.cD2CDInternal(thisCdsResource.getCdsResourceType()));
                if (thisCdsResource.getResourceContents() != null)
                    targetCDSResource.setResourceContents(thisCdsResource.getResourceContents());
                cdsResources.add(targetCDSResource);
            }
        }
        target.setCdsResource(cdsResources);
        target.setFocalPersonId(MappingUtility.iI2FlatId(source.getVmrInput().getPatient().getId()));
        if (source.getCdsContext() != null)
        {
            final CDSContext internalCDSContext = new CDSContext();
            if (source.getCdsContext().getCdsSystemUserType() != null)
                internalCDSContext.setCdsSystemUserType(
                        MappingUtility.cD2CDInternal(source.getCdsContext().getCdsSystemUserType()));
            if (source.getCdsContext().getCdsSystemUserPreferredLanguage() != null)
                internalCDSContext.setCdsSystemUserPreferredLanguage(
                        MappingUtility.cD2CDInternal(source.getCdsContext().getCdsSystemUserPreferredLanguage()));
            if (source.getCdsContext().getCdsInformationRecipientType() != null)
                internalCDSContext.setCdsInformationRecipientType(
                        MappingUtility.cD2CDInternal(source.getCdsContext().getCdsInformationRecipientType()));
            if (source.getCdsContext().getCdsInformationRecipientType() != null)
                internalCDSContext.setCdsInformationRecipientPreferredLanguage(
                        MappingUtility.cD2CDInternal(source.getCdsContext().getCdsSystemUserPreferredLanguage()));
            if (source.getCdsContext().getCdsSystemUserTaskContext() != null)
                internalCDSContext.setCdsSystemUserTaskContext(
                        MappingUtility.cD2CDInternal(source.getCdsContext().getCdsSystemUserTaskContext()));
            target.setCdsContext(internalCDSContext);
        }
    }
}
