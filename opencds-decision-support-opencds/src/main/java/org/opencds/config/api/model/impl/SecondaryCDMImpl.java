package org.opencds.config.api.model.impl;

import java.util.ArrayList;
import java.util.List;

import org.opencds.config.api.model.CDMId;
import org.opencds.config.api.model.SecondaryCDM;
import org.opencds.config.api.model.SupportMethod;

public record SecondaryCDMImpl(CDMId cdmId,
                               SupportMethod supportMethod) implements SecondaryCDM
{
    public static SecondaryCDMImpl create(final CDMId cdmId, final SupportMethod supportMethod)
    {
        return new SecondaryCDMImpl(CDMIdImpl.create(cdmId), supportMethod);
    }

    public static SecondaryCDMImpl create(final SecondaryCDM scdm)
    {
        if (scdm == null)
            return null;
        if (scdm instanceof final SecondaryCDMImpl secondaryCDMImpl)
            return secondaryCDMImpl;
        return create(scdm.getCDMId(), scdm.getSupportMethod());
    }

    public static List<SecondaryCDM> create(final List<SecondaryCDM> secondaryCDMs)
    {
        if (secondaryCDMs == null)
            return null;
        final var scdms = new ArrayList<SecondaryCDM>();
        for (final var scdm : secondaryCDMs)
            scdms.add(create(scdm));
        return scdms;
    }

    public SecondaryCDMImpl
    {
        assert cdmId != null;
        assert supportMethod != null;
    }

    @Override
    public CDMId getCDMId()
    {
        return cdmId;
    }

    @Override
    public SupportMethod getSupportMethod()
    {
        return supportMethod;
    }
}
