package org.opencds.config.api.model.impl;

import org.apache.commons.lang3.StringUtils;
import org.opencds.config.api.model.CDMId;

public record CDMIdImpl(String codeSystem,
                        String code,
                        String version) implements CDMId
{
    public static CDMIdImpl create(final String codeSystem, final String code, final String version)
    {
        return new CDMIdImpl(codeSystem, code, version);
    }

    public static CDMIdImpl create(final CDMId cdmId)
    {
        if (cdmId == null)
            return null;

        if (cdmId instanceof final CDMIdImpl cdmIdImpl)
            return cdmIdImpl;

        return create(cdmId.getCodeSystem(), cdmId.getCode(), cdmId.getVersion());
    }

    public CDMIdImpl
    {
        assert StringUtils.isNotBlank(code);
    }

    @Override
    public String getCodeSystem()
    {
        return codeSystem;
    }

    @Override
    public String getCode()
    {
        return code;
    }

    @Override
    public String getVersion()
    {
        return version;
    }
}
