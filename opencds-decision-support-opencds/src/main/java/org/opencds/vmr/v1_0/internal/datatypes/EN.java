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
public class EN extends ANY
{
    protected List<ENXP> part;
    protected List<EntityNameUse> use;

    public List<ENXP> getPart()
    {
        if (part == null)
            part = new ArrayList<>();
        return this.part;
    }

    public List<EntityNameUse> getUse()
    {
        if (use == null)
            use = new ArrayList<>();
        return this.use;
    }
}
