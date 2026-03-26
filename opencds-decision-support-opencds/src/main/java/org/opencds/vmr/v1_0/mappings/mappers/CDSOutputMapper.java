package org.opencds.vmr.v1_0.mappings.mappers;

import java.util.List;
import java.util.Map;

import org.opencds.common.exceptions.DataFormatException;
import org.opencds.common.exceptions.ImproperUsageException;
import org.opencds.common.exceptions.InvalidDataException;
import org.opencds.common.exceptions.OpenCDSRuntimeException;
import org.opencds.vmr.v1_0.internal.CDSInput;
import org.opencds.vmr.v1_0.internal.VMR;
import org.opencds.vmr.v1_0.internal.datatypes.ANY;
import org.opencds.vmr.v1_0.mappings.utilities.MappingUtility;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CDSOutputMapper
{
    public static void pushOut(final Map<String, List<?>> results, final CDSInput source,
            final org.opencds.vmr.v1_0.schema.CDSOutput target, final String focalPersonId)
            throws DataFormatException, ImproperUsageException, InvalidDataException
    {
        final String _METHODNAME = "pushOut(): ";

        if (source == null)
        {
            final String errStr = _METHODNAME + "improper usage: source supplied is null";
            log.error(errStr);
            throw new ImproperUsageException(errStr);
        }
        else
        {
            if (results.get("VMR") != null)
            {
                @SuppressWarnings("unchecked")
                final List<VMR> sourceVmrList = (List<VMR>) results.get("VMR");
                for (final VMR sourceVmr : sourceVmrList)
                    VMRMapper.pushOut(results, sourceVmr, target, focalPersonId);
            }
            else
                if (results.get("SimpleOutput") != null)
                    target.setSimpleOutput(MappingUtility.aNYInternal2ANY((ANY) results.get("SimpleOutput").getFirst()));
                else
                    throw new OpenCDSRuntimeException(
                            "No outputVMR or simpleOutput found by CdsOutputResultSetBuilder.  No result will be returned");
        }
    }
}
