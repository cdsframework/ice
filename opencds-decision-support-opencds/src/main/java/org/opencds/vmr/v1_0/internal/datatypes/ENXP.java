package org.opencds.vmr.v1_0.internal.datatypes;

import java.util.ArrayList;
import java.util.List;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@EqualsAndHashCode(callSuper = false)
@Getter
@Setter
@ToString
public class ENXP extends XP
{
    protected EntityNamePartType type;
    protected List<EntityNamePartQualifier> qualifier;

    public List<EntityNamePartQualifier> getQualifier()
    {
        if (qualifier == null)
            qualifier = new ArrayList<>();
        return this.qualifier;
    }
}
