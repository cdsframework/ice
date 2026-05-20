package org.opencds.config.api.model;

public record SecondaryCDM(CDMId cdmId,
                           SupportMethod supportMethod)
{
    public SecondaryCDM
    {
        assert cdmId != null;
        assert supportMethod != null;
    }
}
