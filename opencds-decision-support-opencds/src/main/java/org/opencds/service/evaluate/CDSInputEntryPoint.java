package org.opencds.service.evaluate;

import java.io.ByteArrayInputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.xml.transform.stream.StreamSource;

import org.opencds.common.exceptions.InvalidDriDataFormatException;
import org.opencds.common.exceptions.OpenCDSRuntimeException;
import org.opencds.config.api.pool.UnmarshallerFactory;
import org.opencds.vmr.v1_0.mappings.utilities.MappingUtility;
import org.opencds.vmr.v1_0.schema.CDSInput;
import org.opencds.vmr.v1_0.schema.II;

import jakarta.xml.bind.JAXBException;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

@UtilityClass
@Slf4j
public class CDSInputEntryPoint
{
    public static CDSInput buildInput(final byte[] inputPayload)
    {
        log.debug("starting CDSInputEntryPoint");
        final CDSInput cdsInput;

        try
        {
            final var unmarshaller = UnmarshallerFactory.create(CDSInput.class);
            cdsInput = unmarshaller.unmarshal(new StreamSource(new ByteArrayInputStream(inputPayload)), CDSInput.class).getValue();
            validateUniqueIds(cdsInput);
        }
        catch (final JAXBException e)
        {
            log.error(e.getMessage(), e);
            throw new InvalidDriDataFormatException(
                    e.getMessage() + ", therefore unable to unmarshal input Semantic Payload xml string: " + Arrays.toString(
                            inputPayload), e);
        }
        catch (final InvalidDriDataFormatException e)
        {
            throw e;
        }
        catch (final Exception e)
        {
            log.error(e.getMessage(), e);
            throw new OpenCDSRuntimeException(e.getMessage(), e);
        }

        log.debug("finished CDSInputEntryPoint");

        return cdsInput;
    }

    private static void validateUniqueIds(final CDSInput cdsInput)
    {
        visitForIds(cdsInput, new LinkedHashMap<>(), Collections.newSetFromMap(new IdentityHashMap<>()));
    }

    private static void visitForIds(final Object value, final Map<String, String> ids, final java.util.Set<Object> visited)
    {
        if (value == null || value instanceof String || value instanceof Number || value instanceof Boolean
                || value instanceof Enum<?> || value instanceof II || !visited.add(value))
            return;

        if (value instanceof Iterable<?> iterable)
        {
            for (final Object item : iterable)
                visitForIds(item, ids, visited);
            return;
        }

        if (value.getClass().isArray())
        {
            for (int i = 0; i < java.lang.reflect.Array.getLength(value); i++)
                visitForIds(java.lang.reflect.Array.get(value, i), ids, visited);
            return;
        }

        for (Class<?> type = value.getClass(); type != null && type != Object.class; type = type.getSuperclass())
        {
            for (final Field field : type.getDeclaredFields())
            {
                if (Modifier.isStatic(field.getModifiers()) || field.isSynthetic())
                    continue;
                try
                {
                    field.setAccessible(true);
                    final Object fieldValue = field.get(value);
                    if ("id".equals(field.getName()) && fieldValue instanceof II id)
                    {
                        final String flatId = MappingUtility.iI2FlatId(id);
                        final String previousType = ids.putIfAbsent(flatId, value.getClass().getSimpleName());
                        if (previousType != null)
                            throw new InvalidDriDataFormatException(
                                    "Duplicate VMR id '" + flatId + "' found in " + previousType + " and " + value.getClass()
                                            .getSimpleName() + ". Every VMR id (root plus extension) must be unique.");
                    }
                    visitForIds(fieldValue, ids, visited);
                }
                catch (final IllegalAccessException e)
                {
                    throw new IllegalStateException("Unable to inspect VMR identifiers", e);
                }
            }
        }
    }

}
